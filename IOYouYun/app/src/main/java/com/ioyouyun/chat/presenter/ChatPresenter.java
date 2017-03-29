package com.ioyouyun.chat.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hzmc.audioplugin.MediaManager;
import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.biz.ChatRequest;
import com.ioyouyun.chat.biz.ChatRequestImpl;
import com.ioyouyun.chat.biz.OnChatListener;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.FileEntity;
import com.ioyouyun.chat.model.POIInfo;
import com.ioyouyun.chat.view.ChatView;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.receivemsg.msg.MsgUtil;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.message.AudioMessage;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.FileMessage;
import com.ioyouyun.wchat.message.HistoryMessage;
import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.TextMessage;
import com.ioyouyun.wchat.protocol.MetaMessageType;
import com.ioyouyun.wchat.util.HttpCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by YWB on 2016/6/5.
 */
public class ChatPresenter extends BasePresenter<ChatView> {

    private ChatRequest chatRequest;
    private GroupRequest groupRequest;
    private MyInnerReceiver receiver;
    private Handler handler;
    public List<ChatMsgEntity> msgList = new ArrayList<>();

    public ChatPresenter() {
        handler = new Handler(Looper.getMainLooper());
        chatRequest = new ChatRequestImpl();
        groupRequest = new GroupRequestImpl();
        registerReceiver();
        EventBus.getDefault().register(this);
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.MSG_TYPE_RECEIVE,
                FunctionUtil.MSG_TYPE_DOWNLOAD_FILE_PRO, FunctionUtil.MSG_TYPE_SEND_FILE_PRO);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        if (receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    public void onDestroy() {
        unregisterReceiver();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 进入聊天室
     * @param roomId
     */
    public void enterChatRoom(String roomId){
        groupRequest.enterChatRoom(roomId, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.enterChatRoom(result);
                    }
                });
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.enterChatRoom(false);
                    }
                });
            }
        });
    }

    /**
     * 退出聊天室
     * @param roomId
     */
    public void exitChatRoom(String roomId){
        groupRequest.exitChatRoom(roomId, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.exitChatRoom(result);
                    }
                });
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.enterChatRoom(false);
                    }
                });
            }
        });
    }

    /**
     * 发送位置
     *
     * @param toUid
     * @param poiInfo
     * @param nickName
     * @param convType
     */
    public void sendLocation(final String toUid, final POIInfo poiInfo, final String nickName, final ConvType convType) {
        final String msgId = FunctionUtil.genLocalMsgId();
        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String text = MsgUtil.getPositionJsonStr(poiInfo);
        final String body = MsgUtil.getSysMsgBody(text).toString();

        chatRequest.sendMixedText(msgId, toUid, text, convType, padding, new OnChatListener() {

            @Override
            public void onSuccess() {
                Logger.v("发送位置成功");
                if (mView != null)
                    mView.clearChatContent();

                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), body, ChatMsgEntity.CHAT_TYPE_SEND_POS);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setConvType(convType);
                entity.setPoiInfo(poiInfo);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    /**
     * 发送文本
     *
     * @param toUid
     * @param text
     * @param nickName
     * @param convType
     */
    public void sendText(final String toUid, final String text, final String nickName, final ConvType convType) {
        final String msgId = FunctionUtil.genLocalMsgId();
        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        chatRequest.sendText(msgId, toUid, text, convType, padding, new OnChatListener() {

            @Override
            public void onSuccess() {
                Logger.v("发送成功");
                if (mView != null)
                    mView.clearChatContent();

                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), text, ChatMsgEntity.CHAT_TYPE_SEND_TEXT);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    /**
     * 发送表情
     *
     * @param toUid
     * @param text
     * @param nickName
     * @param convType
     */
    public void sendEmoji(final String toUid, final String text, final String nickName, final ConvType convType) {
        final String msgId = FunctionUtil.genLocalMsgId();
        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        chatRequest.sendEmoji(msgId, toUid, text, convType, padding, new OnChatListener() {

            @Override
            public void onSuccess() {
                Logger.v("发送成功");
                if (mView != null)
                    mView.clearChatContent();

                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), text, ChatMsgEntity.CHAT_TYPE_SEND_EMOJI);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    /**
     * 发送图片
     *
     * @param toUid
     * @param nickName
     * @param filePath
     * @param fileName
     * @param convType
     */
    public void sendImage(final String toUid, final String nickName, String filePath, String fileName, final ConvType convType) {
        Logger.v("toUid:" + toUid + "|filePath:" + filePath + "|fileName:" + fileName);
        final String msgId = FunctionUtil.genLocalMsgId();

        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] thumbnail = null;

        String path = null;
        FileEntity fileEntity = null;
        if (!TextUtils.isEmpty(filePath)) {
            String desPath = FileUtil.getChatImagePath(fileName);
            path = FileUtil.compressImage(filePath, desPath);
            int fileLength = 0;
            File file = new File(path);
            if (file.exists())
                fileLength = (int) file.length();
            fileEntity = new FileEntity(null, fileLength, 0, path);
        }

        String thumbnailPath = "";
        if (thumbnail == null && path != null) {
            thumbnail = FileUtil.genSendImgThumbnail(path);
            if (thumbnail != null) {
                thumbnailPath = FileUtil.getThumbnailPath(FunctionUtil.uid, msgId);
                FileUtil.saveImg(thumbnail, thumbnailPath); //保存缩略图
            }
        }

        String sendPath = "";
        if (fileEntity.getFileLocal() != null) {
            sendPath = fileEntity.getFileLocal();
        }

        final String tp = thumbnailPath;
        final FileEntity fe = fileEntity;
        chatRequest.sendFile(msgId, toUid, sendPath, fileName, convType, MetaMessageType.image, padding, thumbnail, new OnChatListener() {
            @Override
            public void onSuccess() {
                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), "", ChatMsgEntity.CHAT_TYPE_SEND_IMAGE);
                entity.setMsgId(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setImgThumbnail(tp);
                entity.setFileEntity(fe);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });
    }

    public void sendFile(final String toUid, final String nickName, String filePath, final String fileName, final ConvType convType){
        Logger.v("toUid:" + toUid + "|filePath:" + filePath + "|fileName:" + fileName);
        final String msgId = FunctionUtil.genLocalMsgId();

        JSONObject object = new JSONObject();
        try {
            if (ConvType.single == convType)
                object.put("nickname", FunctionUtil.nickname);
            else
                object.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] padding = null;
        try {
            padding = object.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        FileEntity fileEntity = null;
        if (!TextUtils.isEmpty(filePath)) {
            int fileLength = 0;
            File file = new File(filePath);
            if (file.exists())
                fileLength = (int) file.length();
            fileEntity = new FileEntity(null, fileLength, 0, filePath);
        }

        final FileEntity fe = fileEntity;
        chatRequest.sendFile(msgId, toUid, filePath, fileName, convType, MetaMessageType.file, padding, null, new OnChatListener() {
            @Override
            public void onSuccess() {
                ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), fileName, ChatMsgEntity.CHAT_TYPE_SEND_FILE);
                entity.setMsgId(msgId);
                FunctionUtil.sendFileStatus.add(msgId);
                entity.setOppositeId(toUid);
                entity.setFromId(FunctionUtil.uid);
                entity.setToId(toUid);
                entity.setName(nickName);
                entity.setFileEntity(fe);
                entity.setConvType(convType);
                entity.setUnreadMsgNum(0);
                refreshAdapter(entity);
                insertDatas(entity);
            }

            @Override
            public void onFaild() {

            }
        });

    }

    public void downloadFile(ChatMsgEntity entity){
        FileEntity fileEntity = entity.getFileEntity();
        String downloadPath = FileUtil.getChatFilePath(entity.getText());
        Logger.d("下载路径：" + downloadPath);
        chatRequest.downloadFile(fileEntity.getFileId(), downloadPath, fileEntity.getFileLength(), fileEntity.getPieceSize(), new OnChatListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFaild() {
                FunctionUtil.toastMessage("下载失败");
            }
        });
    }

    /**
     * 本地数据库聊天数据
     *
     * @param tid
     */
    public void refreshLocalData(String tid) {
        String name = FunctionUtil.jointTableName(tid);
        List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
        msgList.clear();
        msgList.addAll(list);
        if (mView != null) {
            mView.showChatMsgList(msgList);
            mView.setChatSelection(msgList.size() - 1);
        }
    }

    /**
     * 获取历史记录
     *
     * @param toUid
     * @param convType
     */
    public void getHistory(String toUid, ConvType convType) {
        if (msgList == null)
            return;

        long time = -1;
        if (msgList.size() > 0) {
            time = msgList.get(0).getTimestamp() / 1000;
        }
        chatRequest.getChatHistory(toUid, time, 10, convType, new HttpCallback() {
            @Override
            public void onResponse(String s) {

            }

            @Override
            public void onResponseHistory(List hlist) {
                refreshComplete();
                List<HistoryMessage> list = hlist;
                if (list == null || list.size() == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FunctionUtil.toastMessage("没有更多数据");
                        }
                    });
                    return;
                }

                for (HistoryMessage message : list) {
                    if (NoticeType.textmessage == message.type) {
                        TextMessage textMessage = (TextMessage) message.message;
                        receiveText(textMessage);
                    } else if (NoticeType.audiomessage == message.type) {
                        AudioMessage audioMessage = (AudioMessage) message.message;
                        receiveAudio(audioMessage);
                    } else if (NoticeType.filemessage == message.type) {
                        FileMessage fileMessage = (FileMessage) message.message;
                        receiveFile(fileMessage);
                    } else if (NoticeType.emotion == message.type) {
                        TextMessage textMessage = (TextMessage) message.message;
                        receiveEmoji(textMessage);
                    } else if (NoticeType.mixedtextmessage == message.type) {
                        TextMessage textMessage = (TextMessage) message.message;
                        receiveMixedText(textMessage);
                    }
                }
                historyRefreshAdapter();
            }

            @Override
            public void onError(Exception e) {
                Logger.v("error:" + e.getMessage());
                refreshComplete();
            }
        });
    }

    private void receiveMixedText(TextMessage textMessage) {
        String msg = textMessage.text;
        int type = 0;
        if (msg != null)
            type = MsgUtil.getSysMsgType(msg);
        if (type != 0 && msg != null) {
            if (type == MsgUtil.MSGT_POS) {
                JSONObject obj = MsgUtil.getSysMsgBody(msg);
                POIInfo poiInfo = ParseJson.parseJson2T(obj, POIInfo.class);

                String toUid = textMessage.touid;
                if (ConvType.group == textMessage.convType) {
                    toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
                }
                int msgType = ChatMsgEntity.CHAT_TYPE_RECV_POS;
                if (FunctionUtil.uid.equals(textMessage.fromuid))
                    msgType = ChatMsgEntity.CHAT_TYPE_SEND_POS;
                ChatMsgEntity entity = new ChatMsgEntity(textMessage.time, obj.toString(), msgType);
                entity.setPoiInfo(poiInfo);
                entity.setMsgId(textMessage.msgId);
                entity.setFromId(textMessage.fromuid);
                entity.setToId(toUid);
                entity.setConvType(textMessage.convType);
                entity.setUnreadMsgNum(0);
                entity.setShowTime(true);

                String nickname = "";
                if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
                    nickname = FunctionUtil.nickname;
                } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
                    try {
                        String padding = new String(textMessage.padding, "utf-8");
                        JSONObject object = new JSONObject(padding);
                        nickname = object.getString("nickname");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                entity.setName(nickname);

                insertHistoryToDB(entity);
            }
        }
    }

    private void receiveFile(FileMessage fileMessage) {
        String thumbnailPath = "";
        if (null != fileMessage.thumbData) {
            thumbnailPath = FileUtil.getThumbnailPath(fileMessage.fromuid, fileMessage.msgId);
            FileUtil.saveImg(fileMessage.thumbData, thumbnailPath); //保存缩略图
        }
        FileEntity fileEntity = new FileEntity(fileMessage.fileId, fileMessage.fileLength, fileMessage.pieceSize, null);

        String toUid = fileMessage.touid;
        if (ConvType.group == fileMessage.convType) {
            toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
        }
        int msgType;
        if(MetaMessageType.file == fileMessage.type){
            if (FunctionUtil.uid.equals(fileMessage.fromuid))
                msgType = ChatMsgEntity.CHAT_TYPE_SEND_FILE;
            else
                msgType = ChatMsgEntity.CHAT_TYPE_RECV_FILE;
        }else{
            if (FunctionUtil.uid.equals(fileMessage.fromuid))
                msgType = ChatMsgEntity.CHAT_TYPE_SEND_IMAGE;
            else
                msgType = ChatMsgEntity.CHAT_TYPE_RECV_IMAGE;
        }

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setFromId(fileMessage.fromuid);
        entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setImgThumbnail(thumbnailPath);
        entity.setText(fileMessage.filename);
        entity.setFileEntity(fileEntity);
        entity.setMsgType(msgType);
        entity.setConvType(fileMessage.convType);
        entity.setUnreadMsgNum(0);
        entity.setShowTime(true);

        String nickname = "";
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE || entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_FILE) {
            nickname = FunctionUtil.nickname;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE || entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_FILE) {
            try {
                String padding = new String(fileMessage.padding, "utf-8");
                JSONObject object = new JSONObject(padding);
                nickname = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entity.setName(nickname);

        insertHistoryToDB(entity);
    }

    private Object audioLockObj = new Object();
    private void receiveAudio(AudioMessage audioMessage) {
        if (audioMessage == null)
            return;

        FileOutputStream fis = null;
        try {
            String toUid = audioMessage.touid;
            Log.d("Bill", "toUid:" + toUid);
            if (ConvType.group == audioMessage.convType) {
                toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
            }
            int msgType = ChatMsgEntity.CHAT_TYPE_RECV_AUDIO;
            if (FunctionUtil.uid.equals(audioMessage.fromuid))
                msgType = ChatMsgEntity.CHAT_TYPE_SEND_AUDIO;

            String touchId = audioMessage.fromuid;
            String padding = new String(audioMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            String durtion = object.getString("duration");
            String nickname = "";
            if (msgType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
                nickname = FunctionUtil.nickname;
            } else if (msgType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
                nickname = object.getString("nickname");
            }

            String filePath = FileUtil.getUserAudioPath(touchId);
            String audioName = filePath + audioMessage.spanId + ".amr";
            File audioNameFile = new File(audioName);
            if (audioNameFile.exists()) {
                audioNameFile.delete();
            }

            synchronized (audioLockObj) {
                fis = new FileOutputStream(audioNameFile, true);
                if (audioMessage.audioData != null)
                    fis.write(audioMessage.audioData);
            }

            Logger.v("语音存储在：" + audioName);

            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setTimestamp(audioMessage.time);
            entity.setText(audioName);
            entity.setMsgType(msgType);
            entity.setMsgId(audioMessage.spanId);
            entity.setFromId(audioMessage.fromuid);
            entity.setToId(toUid);
            entity.setAudioTime(durtion);
            entity.setName(nickname);
            entity.setConvType(audioMessage.convType);
            entity.setUnreadMsgNum(0);
            entity.setShowTime(true);
            entity.setAudioRead(true);

            insertHistoryToDB(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.flush();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveText(TextMessage textMessage) {
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
        }
        int msgType = ChatMsgEntity.CHAT_TYPE_RECV_TEXT;
        if (FunctionUtil.uid.equals(textMessage.fromuid))
            msgType = ChatMsgEntity.CHAT_TYPE_SEND_TEXT;
        ChatMsgEntity entity = new ChatMsgEntity(textMessage.time, textMessage.text, msgType);
        entity.setMsgId(textMessage.msgId);
        entity.setFromId(textMessage.fromuid);
        entity.setToId(toUid);
        entity.setConvType(textMessage.convType);
        entity.setUnreadMsgNum(0);
        entity.setShowTime(true);

        String nickname = "";
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
            nickname = FunctionUtil.nickname;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
            try {
                String padding = new String(textMessage.padding, "utf-8");
                JSONObject object = new JSONObject(padding);
                nickname = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entity.setName(nickname);

        insertHistoryToDB(entity);
    }

    private void receiveEmoji(TextMessage textMessage) {
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            toUid = toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$"));
        }
        int msgType = ChatMsgEntity.CHAT_TYPE_RECV_EMOJI;
        if (FunctionUtil.uid.equals(textMessage.fromuid))
            msgType = ChatMsgEntity.CHAT_TYPE_SEND_EMOJI;
        ChatMsgEntity entity = new ChatMsgEntity(textMessage.time, textMessage.text, msgType);
        entity.setMsgId(textMessage.msgId);
        entity.setFromId(textMessage.fromuid);
        entity.setToId(toUid);
        entity.setConvType(textMessage.convType);
        entity.setUnreadMsgNum(0);
        entity.setShowTime(true);

        String nickname = "";
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_EMOJI) {
            nickname = FunctionUtil.nickname;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_EMOJI) {
            try {
                String padding = new String(textMessage.padding, "utf-8");
                JSONObject object = new JSONObject(padding);
                nickname = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entity.setName(nickname);

        insertHistoryToDB(entity);
    }

    synchronized private void insertHistoryToDB(ChatMsgEntity entity) {
        historyCount++;

        String oppositeId;
        if(entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT ||
                entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_EMOJI ||
                entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE ||
                entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_FILE ||
                entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO ||
                entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_POS ||
                ConvType.group == entity.getConvType() || ConvType.room == entity.getConvType()){
            oppositeId = entity.getToId();
        }else{
            oppositeId = entity.getFromId();
        }
        String name = FunctionUtil.jointTableName(oppositeId);
        YouyunDbManager.getIntance().insertChatMessage(entity, name);
        if(ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()){
            YouyunDbManager.getIntance().insertFileInfo(entity.getMsgId(), entity.getFileEntity());
        }

        msgList.add(0, entity);
        if (msgList.size() > 1) {
            if (msgList.get(1).getTimestamp() - msgList.get(0).getTimestamp() <= FunctionUtil.MSG_TIME_SEPARATE) {
                msgList.get(1).setShowTime(false);
                YouyunDbManager.getIntance().updateShowTime(msgList.get(1), name);
            }
        }

    }

    /**
     * 储存数据
     *
     * @param entity
     */
    private void insertDatas(ChatMsgEntity entity) {
        String name = FunctionUtil.jointTableName(entity.getToId());
        YouyunDbManager.getIntance().insertRecentContact(entity);
        YouyunDbManager.getIntance().insertChatMessage(entity, name);
        if(ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()){
            YouyunDbManager.getIntance().insertFileInfo(entity.getMsgId(), entity.getFileEntity());
        }
    }

    /**
     * 刷新聊天列表，计算时间显示规则
     *
     * @param entity
     */
    private void refreshAdapter(ChatMsgEntity entity) {
        int index = msgList.size();
        msgList.add(index, entity);
        int preIndex = index - 1;
        if (preIndex >= 0) {
            if (msgList.get(index).getTimestamp() - msgList.get(preIndex).getTimestamp() > FunctionUtil.MSG_TIME_SEPARATE) {
                msgList.get(index).setShowTime(true);
                msgList.set(index, entity);
            }
        } else {
            msgList.get(index).setShowTime(true);
            msgList.set(index, entity);
        }

        if (mView != null) {
            mView.showChatMsgList(msgList);
            mView.setChatSelection(msgList.size() - 1);
        }
    }

    /**
     * 接收消息刷新聊天列表
     *
     * @param entity
     */
    private void recvMsgRefreshAdapter(ChatMsgEntity entity) {
        if(FunctionUtil.TO_UID.equals(entity.getOppositeId())) {
            msgList.add(entity);
            if (mView != null) {
                mView.showChatMsgList(msgList);
                mView.setChatSelection(msgList.size() - 1);
            }
        }
    }

    /**
     * 刷新历史聊天列表
     */
    private void historyRefreshAdapter() {
        Logger.v("historyCount:" + historyCount);
        if (historyCount > 0) {
            final int count = historyCount;
            historyCount = 0;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mView != null) {
                        mView.showChatMsgList(msgList);
                        mView.setChatSelection(count);
                    }
                }
            });
        }
    }
    int historyCount = 0;

    /**
     * 取消下拉加载进度条
     */
    private void refreshComplete() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.onCompleteLoad();
                }
            }
        });
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    private String getNowDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }

    /**
     * 按住说话
     *
     * @param touid
     * @param nickName
     * @param convType
     */
    synchronized public void startRealTimeRecord(String touid, String nickName, ConvType convType, Handler micHandler) {
        String spanId = FunctionUtil.genLocalMsgId();
        String audioName = FileUtil.getUserAudioPath(FunctionUtil.uid) + spanId + ".amr";
        Logger.v("audioName:" + audioName);
        RecImpl recordCallBack = new RecImpl(spanId, audioName, touid, nickName, convType, micHandler);
        MediaManager.getMediaPlayManager().setRecordCallBack(recordCallBack);
        MediaManager.getMediaPlayManager().startRealTimeRecord(null, audioName);
    }

    // 废弃此次录音
    private boolean discardRecord = false;

    synchronized public void discardRecording(boolean discardRecord) {
        this.discardRecord = discardRecord;
    }

    synchronized public void stopRealTimeRecord() {
        MediaManager.getMediaPlayManager().stopRealTimeRecord();
    }

    class RecImpl implements MediaManager.RecordCallBack {

        private String spanId;
        private String audioName; // 语音的地址+名称
        private String toUid;
        private String nickName;
        private ConvType convType;
        private Handler micHandler;

        public RecImpl(String spanId, String audioName, String toUid, String nickName, ConvType convType, Handler micHandler) {
            this.spanId = spanId;
            this.audioName = audioName;
            this.toUid = toUid;
            this.nickName = nickName;
            this.convType = convType;
            this.micHandler = micHandler;
        }

        @Override
        synchronized public void recordStartCallback(boolean bstarted) {
            Logger.v("recordStartCallback:" + bstarted);
        }

        @Override
        synchronized public void recordAudioData(byte[] buffer, int size, int seq) {
            String msgId = FunctionUtil.genLocalMsgId();
            chatRequest.sendVoiceContinue(msgId, toUid, spanId, seq, false, buffer, convType, null, null);
        }

        @Override
        synchronized public void recordStopCallback(long totalsize, int seqcount) {
            final String msgId = FunctionUtil.genLocalMsgId();

            if (discardRecord) {
                chatRequest.sendVoiceContinue(msgId, toUid, spanId, Integer.MAX_VALUE, false, new byte[]{0},
                        convType, null, null);
                FileUtil.removeFile(audioName);
                return;
            }

            int al = (int) (Math.ceil(seqcount / 2.0));
            if (al <= 0)
                return;
            final String audioLength = String.valueOf(al);
            JSONObject object = new JSONObject();
            try {
                object.put("duration", audioLength);
                if (ConvType.single == convType)
                    object.put("nickname", FunctionUtil.nickname);
                else
                    object.put("nickname", nickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] padding = null;
            try {
                padding = object.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            chatRequest.sendVoiceContinue(msgId, toUid, spanId, seqcount + 1, true, new byte[]{0}, convType, padding, new OnChatListener() {
                @Override
                public void onSuccess() {
                    Logger.v("发送成功");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChatMsgEntity entity = new ChatMsgEntity(System.currentTimeMillis(), audioName, ChatMsgEntity.CHAT_TYPE_SEND_AUDIO);
                            entity.setMsgId(msgId);
                            entity.setOppositeId(toUid);
                            entity.setFromId(FunctionUtil.uid);
                            entity.setToId(toUid);
                            entity.setName(nickName);
                            entity.setAudioTime(audioLength);
                            entity.setConvType(convType);
                            entity.setUnreadMsgNum(0);
                            entity.setAudioRead(true);
                            refreshAdapter(entity);
                            insertDatas(entity);
                        }
                    });
                }

                @Override
                public void onFaild() {
                    FileUtil.removeFile(audioName);
                }
            });
        }

        @Override
        synchronized public void recordVolumeCallback(long value) {
            /*int v = (int) ((value - 30) / 2);
            if (v > 13) {
                v = 13;
            } else if (v < 0) {
                v = 0;
            }*/

            int rangeFloor = 28;
            int rangeCeiling = 45;
            int v = (int) (((value - rangeFloor) * 12) / (rangeCeiling - rangeFloor));
            if (v > 14) {
                v = 14;
            } else if (v <= 1) {
                v = 1;
            }

            Message message = micHandler.obtainMessage();
            message.what = v - 1;
            micHandler.sendMessage(message);
        }
    }

    /**
     * EventBus更新聊天数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chatListEvent(MessageEvent.DownloadImageEvent event) {
        ChatMsgEntity entity = msgList.get(event.position);
        entity.setFileEntity(event.fileEntity);
        msgList.set(event.position, entity);
    }

    /**
     * EventBus 刷新聊天界面
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshChatUIEvent(MessageEvent.RefreshChatListEvent event) {
        Logger.d("gid:" + event.touid);
        refreshLocalData(event.touid);
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FunctionUtil.MSG_TYPE_RECEIVE.equals(action)) {
                ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(FunctionUtil.TYPE_MSG);
                recvMsgRefreshAdapter(entity);
            } else if (FunctionUtil.MSG_TYPE_SEND_FILE_PRO.equals(action)) {
                // 文件上传进度
                String fileId = intent.getStringExtra(FunctionUtil.FILE_FILEID);
                int progress = intent.getIntExtra(FunctionUtil.FILE_PROGRESS, 0);
                if(progress == 100){
                    if(FunctionUtil.sendFileStatus.contains(fileId))
                        FunctionUtil.sendFileStatus.remove(fileId);
                }
                for (int i = msgList.size() - 1; i >= 0; i--) {
                    if(msgList.get(i).getMsgId().equals(fileId)){
                        msgList.get(i).setFileProgress(progress);
                        break;
                    }
                }
                if (mView != null) {
                    mView.showChatMsgList(msgList);
                    mView.setChatSelection(msgList.size() - 1);
                }

            }else if (FunctionUtil.MSG_TYPE_DOWNLOAD_FILE_PRO.equals(action)) {
                // 文件下载进度
                String fileId = intent.getStringExtra(FunctionUtil.FILE_FILEID);
                int progress = intent.getIntExtra(FunctionUtil.FILE_PROGRESS, 0);
                for (int i = msgList.size() - 1; i >= 0; i--) {
                    FileEntity fileEntity = msgList.get(i).getFileEntity();
                    if(fileEntity != null && fileEntity.getFileId() != null && ChatMsgEntity.CHAT_TYPE_RECV_FILE == msgList.get(i).getMsgType()){
                        if(fileEntity.getFileId().equals(fileId)){
                            msgList.get(i).setFileProgress(progress);
                            if(FunctionUtil.downloadFileStatus.get(fileId) != null && FunctionUtil.downloadFileStatus.get(fileId) == 1){
                                FunctionUtil.downloadFileStatus.put(fileId, 3);
                            }
                            if(progress == 100){
                                FunctionUtil.downloadFileStatus.put(fileId, 2);
                                String downloadPath = FileUtil.getChatFilePath(msgList.get(i).getText());
                                msgList.get(i).getFileEntity().setFileLocal(downloadPath);
                                YouyunDbManager.getIntance().updateChatFileInfo(msgList.get(i).getMsgId(), downloadPath);
                            }
                            break;
                        }
                    }
                }
                if (FunctionUtil.downloadFileStatus.get(fileId) != null && mView != null) {
                    mView.showChatMsgList(msgList);
                    mView.setChatSelection(msgList.size() - 1);
                }

            }

        }
    }

}
