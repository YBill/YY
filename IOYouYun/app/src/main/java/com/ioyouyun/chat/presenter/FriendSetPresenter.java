package com.ioyouyun.chat.presenter;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.view.FriendSetView;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.utils.FunctionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 卫彪 on 2016/11/4.
 */
public class FriendSetPresenter extends BasePresenter<FriendSetView> {

    private Handler handler;

    public FriendSetPresenter() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 删除本地数据
     *
     * @param uid
     */
    public void clearLocalData(String uid) {
        String name = FunctionUtil.jointTableName(uid);
        List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
        if (YouyunDbManager.getIntance().removeChatImageMsg(name)){
            for (ChatMsgEntity entity : list){
                if(ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()){
                    YouyunDbManager.getIntance().deleteFileInfoById(entity.getMsgId());
                }
            }
            FunctionUtil.toastMessage("清空成功");

            MessageEvent.RefreshChatListEvent event = new MessageEvent.RefreshChatListEvent();
            event.touid = uid;
            EventBus.getDefault().post(event);
        }
    }


}
