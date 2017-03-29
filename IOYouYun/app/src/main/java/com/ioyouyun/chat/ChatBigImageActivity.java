package com.ioyouyun.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ioyouyun.R;
import com.ioyouyun.chat.biz.ChatRequest;
import com.ioyouyun.chat.biz.ChatRequestImpl;
import com.ioyouyun.chat.biz.OnChatListener;
import com.ioyouyun.chat.model.FileEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 卫彪 on 2016/6/20.
 */
public class ChatBigImageActivity extends Activity {

    private final static String MSG_ID = "msg_id";
    private final static String FILE_ENTITY = "file_entity";
    private final static String CHAT_POSITION = "chat_position";
    private ImageView imageView;
    private ChatRequest request;
    private MyInnerReceiver receiver;
    private String downloadPath;
    private String msgId;
    private FileEntity fileEntity;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_big_image);
        imageView = (ImageView) findViewById(R.id.iv_bigimg);

        registerReceiver();
        request = new ChatRequestImpl();

        getIntentExtra();

        showImg(fileEntity);
    }

    private void setChatPicInfo(String imgPath){
        fileEntity.setFileLocal(imgPath);
        notifyChatList(fileEntity);
        YouyunDbManager.getIntance().updateChatFileInfo(msgId, imgPath);
    }

    /**
     * 通知聊天数据更新
     */
    private void notifyChatList(FileEntity fileEntity){
        MessageEvent.DownloadImageEvent event = new MessageEvent.DownloadImageEvent();
        event.position = position;
        event.fileEntity = fileEntity;
        EventBus.getDefault().post(event);
    }

    private void loadLocalImage(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }

    private void showImg(FileEntity fileEntity) {
        if(TextUtils.isEmpty(fileEntity.getFileLocal())){
            downLoadImg(fileEntity);
        }else{
            Bitmap bitmap = BitmapFactory.decodeFile(fileEntity.getFileLocal());
            imageView.setImageBitmap(bitmap);
        }
    }

    private void downLoadImg(FileEntity fileEntity){
        downloadPath = FileUtil.getChatImagePath(fileEntity.getFileId()) + ".png";
        request.downloadFile(fileEntity.getFileId(), downloadPath, fileEntity.getFileLength(), fileEntity.getPieceSize(), new OnChatListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFaild() {
                FunctionUtil.toastMessage("下载失败");
            }
        });
    }

    public static void startActivity(Context activity, String msgId, FileEntity fileEntity, int position){
        Intent intent = new Intent(activity, ChatBigImageActivity.class);
        intent.putExtra(MSG_ID, msgId);
        intent.putExtra(FILE_ENTITY, fileEntity);
        intent.putExtra(CHAT_POSITION, position);
        activity.startActivity(intent);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        if(intent != null){
            msgId = intent.getStringExtra(MSG_ID);
            fileEntity = intent.getParcelableExtra(FILE_ENTITY);
            position = intent.getIntExtra(CHAT_POSITION, 0);
        }
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver(){
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.MSG_TYPE_DOWNLOAD_FILE_PRO);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver(){
        if(receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(FunctionUtil.MSG_TYPE_DOWNLOAD_FILE_PRO.equals(action)){
                String fileId = intent.getStringExtra(FunctionUtil.FILE_FILEID);
                int progress = intent.getIntExtra(FunctionUtil.FILE_PROGRESS, 0);
                String filePath = FileUtil.getChatImagePath(fileId) + ".png";
                Logger.v("progress:" + progress);
                if(progress == 100){
                    // 下载完成
                    Logger.v("下载完成");
                    if(filePath.equals(downloadPath)){
                        setChatPicInfo(filePath);
                        loadLocalImage(filePath);
                    }
                }
            }
        }
    }
}
