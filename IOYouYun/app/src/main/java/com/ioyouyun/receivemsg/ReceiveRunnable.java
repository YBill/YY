package com.ioyouyun.receivemsg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ioyouyun.ForegroundCallbacks;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.FileEntity;
import com.ioyouyun.chat.model.POIInfo;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.media.BeInviteActivity;
import com.ioyouyun.media.IncomingCallActivity;
import com.ioyouyun.receivemsg.msg.MsgBodyTemplate;
import com.ioyouyun.receivemsg.msg.MsgUtil;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.utils.PushSharedUtil;
import com.ioyouyun.utils.SoundVibrateUtil;
import com.ioyouyun.wchat.message.AudioMessage;
import com.ioyouyun.wchat.message.CmdType;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.FileMessage;
import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.NotifyCenter;
import com.ioyouyun.wchat.message.SendBackMessage;
import com.ioyouyun.wchat.message.StatusType;
import com.ioyouyun.wchat.message.SystemMessage;
import com.ioyouyun.wchat.message.TextMessage;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.message.WeimiNotice;
import com.ioyouyun.wchat.protocol.MetaMessageType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class ReceiveRunnable implements Runnable {

    private static Object audioLockObj = new Object();
    public static Map<String, List<AudioMessage>> audioMessageReceive = new ConcurrentHashMap<>();
    public static Map<String, List<Integer>> fileSend = new ConcurrentHashMap<>();
    public static Map<String, Integer> fileSendCount = new ConcurrentHashMap<>();
    private Context context;

    public ReceiveRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        WeimiNotice weimiNotice = null;
        while (true) {
            try {
                weimiNotice = (WeimiNotice) NotifyCenter.clientNotifyChannel.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NoticeType type = weimiNotice.getNoticeType();
            Logger.v("消息类型:" + type);

            if (handleExtendActionForMsgReceive(weimiNotice))
                continue;

            if (NoticeType.textmessage == type) {
                textMessageMethod(weimiNotice);
            } else if (NoticeType.emotion == type) {
                emojiMessageMethod(weimiNotice);
            } else if (NoticeType.audiomessage == type) {
                audioMessageMethod(weimiNotice);
            } else if (NoticeType.filemessage == type) {
                fileMessageMethod(weimiNotice);
            } else if (NoticeType.downloadfile == type) {
                downloadMethod(weimiNotice);
            } else if (NoticeType.sendfile == type) {
                sendfileMethod(weimiNotice);
            } else if (NoticeType.wmediastate == type) {
                callMethod(weimiNotice);
            } else if (NoticeType.conferenceResources == type) {
                conferenceMethod(weimiNotice);
            } else if (NoticeType.exception == type) {
                exceptionMethod(weimiNotice);
            } else if (NoticeType.logging == type) {
                loggingMethod(weimiNotice);
            } else if (NoticeType.sendfinished == type) {
                Logger.d("Tag", "消息发送到网关成功,msgId:" + weimiNotice.getWithtag());
            } else if (NoticeType.sendback == type) {
                SendBackMessage sendBackMessage = (SendBackMessage) weimiNotice.getObject();
                Logger.d("Tag", "发送消息成功,msgId:" + weimiNotice.getWithtag() + "|id:" + sendBackMessage.withtag);
            } else {
                Logger.v("noticeType:" + type);
            }

        }

    }

    /**
     * log
     *
     * @param weimiNotice
     */
    private void loggingMethod(WeimiNotice weimiNotice) {
        String log = weimiNotice.getObject().toString();
        Logger.v("logging:" + log);
    }

    /**
     * 异常处理
     *
     * @param weimiNotice
     */
    private void exceptionMethod(WeimiNotice weimiNotice) {
        WChatException wChatException = (WChatException) weimiNotice.getObject();
        int statusCode = wChatException.getStatusCode();
        switch (statusCode) {
            case WChatException.InputParamError: // 输入参数错误
                Logger.v("输入参数错误:" + wChatException.getMessage());
                break;
            case WChatException.CodeException: // 编码处理错误
                Logger.v("编码处理错误:" + wChatException.getMessage());
                Logger.v("编码处理错误:" + wChatException.getCause());
                break;
            case WChatException.InterruptedException: // 请求被中断
                Logger.v("请求被中断:" + wChatException.getMessage());
                Logger.v("请求被中断:" + wChatException.getCause());
                break;
            case WChatException.RequestTimeout: // 会话请求超时
                Logger.v("会话请求超时:" + wChatException.getMessage());
                break;
            case WChatException.BadFileRead: // 文件读取失败
                Logger.v("文件读取失败:" + wChatException.getMessage());
                Logger.v("文件读取失败:" + wChatException.getCause());
                break;
            case WChatException.ResponseError: // 返回结果错误
                Logger.v("返回结果错误:" + wChatException.getMessage());
                Logger.v("返回结果错误:" + wChatException.getCause());
                break;
            case WChatException.AuthFailed: // 认证失败
                Logger.v("认证失败:" + wChatException.getMessage());
                break;
            case WChatException.NetworkInterruption: // 网络中断
                Logger.v("网络中断:" + "NetworkInterruption");
                Logger.v("网络中断:" + wChatException.getMessage());
                break;
            case WChatException.MediaHeartBeatError: // mediaSDK心跳失败
                Logger.v("mediaSDK心跳失败:" + wChatException.getMessage());
                break;
            case WChatException.DataDecodeError: // 数据解析异常
                Logger.v("数据解析异常:" + wChatException.getMessage());
                break;
            case WChatException.SOCKET_CHANNEL_CLOSED: // 网络socket端开
                Logger.v("网络socket端开:" + wChatException.getMessage());
                break;
            default:
                Logger.v("WChatException:" + wChatException.getMessage());
                Logger.v("WChatException:" + wChatException.getStatusCode());
        }
    }

    /**
     * lo
     * 发送文件的回执处理
     *
     * @param weimiNotice
     */
    private void sendfileMethod(WeimiNotice weimiNotice) {
        Logger.d("********** 接收到一条发送文件的返回通知 **********");
        List<Integer> unUploadSliceList = (List<Integer>) weimiNotice.getObject();
        String msgId = weimiNotice.getWithtag();
        /*for (Integer i : unUploadSliceList) {
            Logger.d("可能缺少的分片号：" + i);
        }*/
        if (unUploadSliceList.isEmpty()) {
            fileSend.remove(msgId);
            fileSendCount.remove(msgId);
            Logger.d(msgId + "==文件完成度：100%，发送成功！");
            transportProgress(1, msgId, 100);
            return;
        }

        // 如果收到缺少分片的回执，但只本地已经清理掉fileSendCount的缓存，可认定是很旧的回执，可以丢掉
        if (!fileSendCount.containsKey(msgId)) {
            Logger.d("收到旧的文件上传回执");
            return;
        }
        List<Integer> list = fileSend.get(msgId);
        List<Integer> newList = new LinkedList<>();
        for (int i : unUploadSliceList) { // 排重
            // 如果包含在旧的list中，说明之前就是还未到达的分片
            // 如果不包含在旧的list中，说明之前已经确认到达，但是这个包来得迟了，所以应该去掉重复的包，忽略即可
            if (list.contains(Integer.valueOf(i)) && Integer.valueOf(i) <= Integer.valueOf(fileSendCount.get(msgId))) {
                newList.add(Integer.valueOf(i));
            }
        }
        fileSend.put(msgId, newList);
        double sliceCount = (double) fileSendCount.get(msgId);
        int listSize = newList.size();
        if (0 == listSize) {
            fileSend.remove(msgId);
            fileSendCount.remove(msgId);
            Logger.d(msgId + "--文件完成度：100%，发送成功！");
            transportProgress(1, msgId, 100);
            return;
        } else {
            Logger.d("还有" + listSize + "片没有收到");
            double completed = (sliceCount - listSize) / sliceCount;
            int progress = (int) (completed * 100);
            Logger.d("完成度：" + progress);
            transportProgress(1, msgId, progress);
            /*for (int sliceMissId : newList) {
                Logger.d("实际缺少的分片号：" + sliceMissId);
            }*/
        }
    }

    /**
     * 进度条进度
     *
     * @param action   上传：1，下载：2
     * @param fileId   文件ID
     * @param progress 当前大小
     */
    private void transportProgress(int action, String fileId, int progress) {
        Intent intent = new Intent();
        if (1 == action) {
            // 发送
            intent.setAction(FunctionUtil.MSG_TYPE_SEND_FILE_PRO);
        } else if (2 == action) {
            // 接收
            intent.setAction(FunctionUtil.MSG_TYPE_DOWNLOAD_FILE_PRO);
        }
        intent.putExtra(FunctionUtil.FILE_FILEID, fileId);
        intent.putExtra(FunctionUtil.FILE_PROGRESS, progress);
        intent.setPackage(context.getPackageName());
        BroadCastCenter.getInstance().broadcast(intent);
    }

    ArrayList<String> userList = new ArrayList<>();

    /**
     * Conference
     *
     * @param weimiNotice
     */
    private void conferenceMethod(WeimiNotice weimiNotice) {
        String content = (String) weimiNotice.getObject();
        try {
            JSONObject jsonObject = new JSONObject(content);
            Logger.d("cmd::" + jsonObject.toString());
            if (jsonObject == null || !jsonObject.has("cmd"))
                return;
            Logger.d("cmd:" + jsonObject.getInt("cmd"));
            switch (jsonObject.getInt("cmd")) {
                case CmdType.requesRoom:
                    Logger.v("requesRoom");
                    Intent intent = new Intent();
                    intent.setAction(FunctionUtil.CONFERENCE_REQUEST_ROOM);
                    intent.putExtra(FunctionUtil.CONTENT, content);
                    intent.setPackage(context.getPackageName());
                    BroadCastCenter.getInstance().broadcast(intent);
                    break;
                case CmdType.beingInvited:
                    Logger.v("beingInvited");
                    String currentActivity = FunctionUtil.getCurrentActivity();
                    if (FunctionUtil.BEINVITEACTIVITYACTIVITY_PATH.equals(currentActivity) ||
                            FunctionUtil.CONFERENCEACTIVITY_PATH.equals(currentActivity) ||
                            FunctionUtil.VOIPACTIVITY_PATH.equals(currentActivity)) {
                        return;
                    }
                    String invitedFrom = jsonObject.getString("from");
                    String invitedGroupId = jsonObject.getString("groupid");
                    String invitedTo = jsonObject.getString("to");
                    JSONObject room2 = new JSONObject(jsonObject.getString("room"));
                    String invitedRoomId = room2.getString("id");
                    String invitedRoomKey = room2.getString("key");

                    Logger.d("Bill", "invitedFrom:" + invitedFrom + "|invitedRoomId:" + invitedRoomId + "|invitedRoomKey:" + invitedRoomKey + "|invitedGroupId:" + invitedGroupId);

                    Intent beInviteIntent = new Intent(context, BeInviteActivity.class);
                    beInviteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("invitedFrom", invitedFrom);
                    bundle.putString("key_roomid", invitedRoomId);
                    bundle.putString("key_key", invitedRoomKey);
                    bundle.putString("key_gid", invitedGroupId);
                    beInviteIntent.putExtras(bundle);
                    context.startActivity(beInviteIntent);
                    break;
                case CmdType.inviteUsers:
                    Logger.v("inviteUsers");
                    break;
                case CmdType.list:
                    Logger.v("list");
                    JSONObject cobj = new JSONObject(content);
                    JSONArray array = cobj.optJSONArray("userList");
                    if (array == null)
                        return;
                    userList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        userList.add(array.getString(i));
                    }
                    notifyConferenceList(userList);
                    break;
                case CmdType.notice: {
                    JSONObject contentObj = jsonObject.optJSONObject("content");
                    if (contentObj == null)
                        return;
                    int statusType = contentObj.optInt("type");
                    switch (statusType) {
                        case StatusType.join:
                        case StatusType.leave:
                            JSONArray carray = contentObj.optJSONArray("opuid");
                            userList.clear();
                            for (int i = 0; i < carray.length(); i++) {
                                userList.add(carray.getString(i));
                            }
                            notifyConferenceList(userList);
                            break;
                    }
                }
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通知电话会议人员变化
     *
     * @param list
     */
    private void notifyConferenceList(ArrayList<String> list) {
        Intent listIntent = new Intent();
        listIntent.setAction(FunctionUtil.CONFERENCE_LIST);
        listIntent.putStringArrayListExtra(FunctionUtil.CONTENT, list);
        listIntent.setPackage(context.getPackageName());
        BroadCastCenter.getInstance().broadcast(listIntent);
    }

    /**
     * VoIP
     *
     * @param weimiNotice
     */
    private void callMethod(WeimiNotice weimiNotice) {
        String mediaType = weimiNotice.getObject().toString();
        Logger.d("call cmd:" + mediaType);
        if (FunctionUtil.MSG_TYPE_INCOMINGRECEIVED.equals(mediaType)) {
            String currentActivity = FunctionUtil.getCurrentActivity();
            if (FunctionUtil.VOIPACTIVITY_PATH.equals(currentActivity) ||
                    FunctionUtil.CONFERENCEACTIVITY_PATH.equals(currentActivity)) {
                return;
            }
            Intent intent = new Intent(context, IncomingCallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString(FunctionUtil.INCOMINGNAME, weimiNotice.getWithtag());
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setAction(FunctionUtil.MSG_TYPE_MEDIA_CALL_HEAD + mediaType);
            intent.putExtra(FunctionUtil.INCOMINGNAME, weimiNotice.getWithtag());
            intent.setPackage(context.getPackageName());
            BroadCastCenter.getInstance().broadcast(intent);
        }
    }

    /**
     * 处理额外事件
     *
     * @param weimiNotice
     */
    private boolean handleExtendActionForMsgReceive(WeimiNotice weimiNotice) {
        boolean flag = false;
        long time = System.currentTimeMillis();
        String msg = null;
        int msgType = 0;
        if (weimiNotice.getNoticeType() == NoticeType.system) {
            SystemMessage message = (SystemMessage) weimiNotice.getObject();
            msg = message.content;
            time = message.time;
            msgType = MsgUtil.getSysMsgType(msg);
        } else if (weimiNotice.getNoticeType() == NoticeType.textmessage
                || weimiNotice.getNoticeType() == NoticeType.mixedtextmessage) {
            TextMessage message = (TextMessage) weimiNotice.getObject();
            msg = message.text;
            time = message.time;
            if (msg != null)
                msgType = MsgUtil.getSysMsgType(msg);
        }
        if (msgType != 0 && msg != null) {
            flag = true;
            if (msgType == MsgUtil.MSGT_SYS_NOTI) {// 50002
                String subType = MsgUtil.getSysMsgBodyType(msg);
                if (MsgUtil.MSN_TYPE_APPLY_JOIN_GROUP.equals(subType)) {
                    // 申请加入群
                    JSONObject obj = MsgUtil.getSysMsgBody(msg);
                    MsgBodyTemplate entity = ParseJson.parseJson2T(obj, MsgBodyTemplate.class);
                    if (entity != null) {
                        entity.setDate(getDate(time));

                        String currentActivity = FunctionUtil.getCurrentActivity();
                        if (FunctionUtil.NOTIFYACTIVITYACTIVITY_PATH.equals(currentActivity)) {
                            entity.setIsLook(0);
                        } else {
                            entity.setIsLook(1);
                        }
                        YouyunDbManager.getIntance().insertNotify(entity);

                        Intent intent = new Intent();
                        intent.setAction(FunctionUtil.MSG_TYPE_NOTIFY);
//                    intent.putExtra(FunctionUtil.TYPE_NOTIFY, entity);
                        intent.setPackage(context.getPackageName());
                        BroadCastCenter.getInstance().broadcast(intent);
                    }
                }
            } else if (msgType == MsgUtil.MSGT_POS) {
                locationMethod(weimiNotice);
            }
        }
        return flag;
    }

    /**
     * 对于下载文件的请求
     *
     * @param weimiNotice
     */
    private void downloadMethod(WeimiNotice weimiNotice) {
        Logger.d("********** 接收到文件下载的结果 **********");
        FileMessage fileMessage = (FileMessage) weimiNotice.getObject();
        String fileId = weimiNotice.getWithtag();
        /*List<Integer> list = fileMessage.hasReveive;
        for (int sliceMissId : list) {
            Logger.d("实际已经下载的分片号：" + sliceMissId);
        }*/
        double completed = (fileMessage.hasReveive.size() / (double) fileMessage.limit);
        int progress = (int) (completed * 100);
        Logger.d("文件ID：" + fileId + "--下载进度为：" + progress);
        transportProgress(2, fileId, progress);

    }

    /**
     * 文件
     *
     * @param weimiNotice
     */
    private void fileMessageMethod(WeimiNotice weimiNotice) {
        FileMessage fileMessage = (FileMessage) weimiNotice.getObject();
        if (MetaMessageType.image == fileMessage.type) {
            receiveImage(fileMessage);
        } else if (MetaMessageType.file == fileMessage.type) {
            receiveFile(fileMessage);
        }
    }

    /**
     * 图片
     *
     * @param fileMessage
     */
    private void receiveImage(FileMessage fileMessage) {
        String nickName = "";
        try {
            String padding = new String(fileMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String thumbnailPath = "";
        if (null != fileMessage.thumbData) {
            thumbnailPath = FileUtil.getThumbnailPath(fileMessage.fromuid, fileMessage.msgId);
            FileUtil.saveImg(fileMessage.thumbData, thumbnailPath); //保存缩略图
        }

        FileEntity fileEntity = new FileEntity(fileMessage.fileId, fileMessage.fileLength, fileMessage.pieceSize, null);

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(fileMessage.fromuid);
        String toUid = fileMessage.touid;
        if (ConvType.group == fileMessage.convType)
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        else
            entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setImgThumbnail(thumbnailPath);
        entity.setFileEntity(fileEntity);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_IMAGE);
        entity.setConvType(fileMessage.convType);

        notify(entity);
    }

    /**
     * 文件
     *
     * @param fileMessage
     */
    private void receiveFile(FileMessage fileMessage) {
        String nickName = "";
        try {
            String padding = new String(fileMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FileEntity fileEntity = new FileEntity(fileMessage.fileId, fileMessage.fileLength, fileMessage.pieceSize, null);

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(fileMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(fileMessage.fromuid);
        String toUid = fileMessage.touid;
        if (ConvType.group == fileMessage.convType)
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        else
            entity.setToId(toUid);
        entity.setTimestamp(fileMessage.time);
        entity.setText(fileMessage.filename);
        entity.setFileEntity(fileEntity);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_FILE);
        entity.setConvType(fileMessage.convType);

        notify(entity);
    }

    /**
     * 语音消息
     *
     * @param weimiNotice
     */
    private void audioMessageMethod(WeimiNotice weimiNotice) {
        AudioMessage audioMessage = (AudioMessage) weimiNotice.getObject();
        receiveContinue(audioMessage);
    }

    /**
     * 表情消息
     *
     * @param weimiNotice
     */
    private void emojiMessageMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        Logger.d("Bill", "textMessageMethod:" + textMessage.text + "|fromId:" + textMessage.fromuid + "|toId:" + textMessage.touid);

        String nickName = "";
        try {
            String padding = new String(textMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(textMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(textMessage.fromuid);
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        } else {
            entity.setToId(toUid);
        }
        entity.setTimestamp(textMessage.time);
        entity.setText(textMessage.text);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_EMOJI);
        entity.setConvType(textMessage.convType);

        notify(entity);
    }

    /**
     * 位置消息
     *
     * @param weimiNotice
     */
    private void locationMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        Logger.d("Bill", "locationMethod:" + textMessage.text + "|fromId:" + textMessage.fromuid + "|toId:" + textMessage.touid);

        String nickName = "";
        try {
            String padding = new String(textMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject obj = MsgUtil.getSysMsgBody(textMessage.text);
        POIInfo poiInfo = ParseJson.parseJson2T(obj, POIInfo.class);

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(textMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(textMessage.fromuid);
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        } else {
            entity.setToId(toUid);
        }
        entity.setTimestamp(textMessage.time);
        entity.setText(obj.toString());
        entity.setPoiInfo(poiInfo);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_POS);
        entity.setConvType(textMessage.convType);

        notify(entity);
    }

    /**
     * 文本消息
     *
     * @param weimiNotice
     */
    private void textMessageMethod(WeimiNotice weimiNotice) {
        TextMessage textMessage = (TextMessage) weimiNotice.getObject();
        Logger.d("Bill", "textMessageMethod:" + textMessage.text + "|fromId:" + textMessage.fromuid + "|toId:" + textMessage.touid);

        String nickName = "";
        try {
            String padding = new String(textMessage.padding, "utf-8");
            JSONObject object = new JSONObject(padding);
            nickName = object.getString("nickname");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMsgId(textMessage.msgId);
        entity.setName(nickName);
        entity.setFromId(textMessage.fromuid);
        String toUid = textMessage.touid;
        if (ConvType.group == textMessage.convType) {
            entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
        } else {
            entity.setToId(toUid);
        }
        entity.setTimestamp(textMessage.time);
        entity.setText(textMessage.text);
        entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_TEXT);
        entity.setConvType(textMessage.convType);

        notify(entity);
    }

    private void notify(ChatMsgEntity entity) {
        String toid = entity.getFromId();
        if (ConvType.group == entity.getConvType() || ConvType.room == entity.getConvType())
            toid = entity.getToId();
        entity.setOppositeId(toid);

        if (TextUtils.isEmpty(entity.getName()))
            entity.setName(toid);

        String name = FunctionUtil.jointTableName(toid);
        ChatMsgEntity lastEntity = YouyunDbManager.getIntance().getLastChatMsgEntity(name);
        if (lastEntity == null || lastEntity.getTimestamp() <= 0) {
            entity.setShowTime(true);
        } else {
            if (entity.getTimestamp() - lastEntity.getTimestamp() > FunctionUtil.MSG_TIME_SEPARATE) {
                entity.setShowTime(true);
            }
        }

        String currentActivity = FunctionUtil.getCurrentActivity();
        if (FunctionUtil.CHATACTIVITYACTIVITY_PATH.equals(currentActivity) && FunctionUtil.TO_UID.equals(toid)) {
            entity.setUnreadMsgNum(0);
        } else {
            ChatMsgEntity chatMsgEntity = YouyunDbManager.getIntance().getRecentContactById(toid);
            if (chatMsgEntity == null) {
                entity.setUnreadMsgNum(1);
            } else {
                entity.setUnreadMsgNum(chatMsgEntity.getUnreadMsgNum() + 1);
            }
        }

        setBroadCast(FunctionUtil.MSG_TYPE_RECEIVE, FunctionUtil.TYPE_MSG, entity);

        YouyunDbManager.getIntance().insertRecentContact(entity);
        YouyunDbManager.getIntance().insertChatMessage(entity, name);
        if (ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()) {
            YouyunDbManager.getIntance().insertFileInfo(entity.getMsgId(), entity.getFileEntity());
        }

        // 后台接收消息
        if (ForegroundCallbacks.get().isBackground()) {
            FunctionUtil.sendNotification(context, entity);
        } else {
            // 声音、震动提示
            if (PushSharedUtil.getInstance().getVibration()) {
                SoundVibrateUtil.checkIntervalTimeAndVibrate(context);
            }
            if (PushSharedUtil.getInstance().getSound()) {
                SoundVibrateUtil.checkIntervalTimeAndSound(context);
            }
        }

    }

    /**
     * 发送广播
     *
     * @param action
     * @param key
     * @param entity
     */
    private void setBroadCast(String action, String key, ChatMsgEntity entity) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(key, entity);
        intent.setPackage(context.getPackageName());
        BroadCastCenter.getInstance().broadcast(intent);
    }

    private String getDate(long date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
    }

    private void receiveContinue(AudioMessage audioMessage) {
        List<AudioMessage> spanList;
        if (audioMessageReceive.containsKey(audioMessage.spanId)) {
            spanList = audioMessageReceive.get(audioMessage.spanId);
        } else {
            spanList = new ArrayList<>();
        }
        spanList.add(audioMessage);
        audioMessageReceive.put(audioMessage.spanId, spanList);
        if (audioMessage.spanSequenceNo == Integer.MAX_VALUE) {
            audioMessageReceive.remove(audioMessage.spanId);
        } else if (audioMessage.spanSequenceNo == -1) {
            Logger.v("语音接收结束");
            List<AudioMessage> list = audioMessageReceive.remove(audioMessage.spanId);

            String touchId = null;
            String duration = null;
            String nickName = null;
            try {
                touchId = audioMessage.fromuid;
                String padding = new String(audioMessage.padding, "utf-8");
                JSONObject object = new JSONObject(padding);
                duration = object.getString("duration");
                nickName = object.getString("nickname");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String filePath = FileUtil.getUserAudioPath(touchId);
            String audioName = filePath + audioMessage.spanId + ".amr";
            try {
                FileOutputStream fis = new FileOutputStream(new File(audioName), true);
                synchronized (audioLockObj) {
                    for (AudioMessage am : list) {
                        if (am != null && am.audioData != null)
                            fis.write(am.audioData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Logger.v("语音存储在：" + audioName);

            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setMsgId(audioMessage.spanId);
            entity.setFromId(audioMessage.fromuid);
            entity.setName(nickName);
            String toUid = audioMessage.touid;
            if (ConvType.group == audioMessage.convType)
                entity.setToId(toUid.substring(toUid.indexOf("$") + 1, toUid.lastIndexOf("$")));
            else
                entity.setToId(toUid);
            entity.setText(audioName);
            entity.setTimestamp(audioMessage.time);
            entity.setAudioTime(duration);
            entity.setMsgType(ChatMsgEntity.CHAT_TYPE_RECV_AUDIO);
            entity.setConvType(audioMessage.convType);

            notify(entity);

        }
    }

}
