package com.ioyouyun.chat.model;

import com.ioyouyun.wchat.message.ConvType;

import java.io.Serializable;

/**
 * Created by 卫彪 on 2016/6/14.
 */
public class ChatMsgEntity implements Serializable {

    public static final int CHAT_TYPE_NUM = 12; // 类型总和
    public static final int CHAT_TYPE_RECV_TEXT = 0; // 接收文本
    public static final int CHAT_TYPE_SEND_TEXT = 1; // 发送文本
    public static final int CHAT_TYPE_RECV_AUDIO = 2; // 接收语音
    public static final int CHAT_TYPE_SEND_AUDIO = 3; // 发送语音
    public static final int CHAT_TYPE_RECV_IMAGE = 4; // 接收图片
    public static final int CHAT_TYPE_SEND_IMAGE = 5; // 发送图片
    public static final int CHAT_TYPE_RECV_EMOJI = 6; // 接收表情
    public static final int CHAT_TYPE_SEND_EMOJI = 7; // 发送表情
    public static final int CHAT_TYPE_RECV_FILE = 8; // 接收文件
    public static final int CHAT_TYPE_SEND_FILE = 9; // 发送文件
    public static final int CHAT_TYPE_RECV_POS = 10; // 接收位置
    public static final int CHAT_TYPE_SEND_POS = 11; // 发送位置

    private String msgId; // msg标记唯一一条数据
    private String fromId; // 消息from
    private String toId; // 消息to
    private String oppositeId; // 对方Id,IM其他人uid或gid,用于充当联系人表主键
    private String name; // 昵称
    private long timestamp; // 消息事件,13位
    private String text; // 消息内容
    private String audioTime; // 语音时间
    private String imgThumbnail; // 缩略图
    private FileEntity fileEntity;
    private ConvType convType;
    private int msgType; // 消息类型，取值为上面static final定义的常量
    private boolean isShowTime; // 是否显示时间标签
    private int unreadMsgNum = 0; // 未读消息数
    private boolean isAudioPlaying; // 是否正在播放语音
    private boolean isAudioRead; // 语音是否已读
    private int fileProgress; // 文件进度
    private POIInfo poiInfo; // 位置

    public ChatMsgEntity() {
    }

    public ChatMsgEntity(long timestamp, String text, int msgType) {
        this.timestamp = timestamp;
        this.text = text;
        this.msgType = msgType;
    }

    public POIInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(POIInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public int getFileProgress() {
        return fileProgress;
    }

    public void setFileProgress(int fileProgress) {
        this.fileProgress = fileProgress;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public boolean isAudioRead() {
        return isAudioRead;
    }

    public void setAudioRead(boolean audioRead) {
        isAudioRead = audioRead;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }

    public boolean isShowTime() {
        return isShowTime;
    }

    public void setShowTime(boolean showTime) {
        isShowTime = showTime;
    }

    public int getAudioReadInt(ChatMsgEntity entity) {
        int isRead = 0; // 未读
        if (entity.isAudioRead)
            isRead = 1; // 已读
        return isRead;
    }

    public boolean getAudioReadBoolean(int isRead) {
        boolean isAudioRead = false;
        if (isRead == 1)
            isAudioRead = true;
        return isAudioRead;
    }

    public int getShowTimeInt(ChatMsgEntity entity) {
        int showTime = 0; // 不显示
        if (entity.isShowTime)
            showTime = 1; // 显示
        return showTime;
    }

    public boolean getShowTimeBoolean(int showTime) {
        boolean isShowTime = false;
        if (showTime == 1)
            isShowTime = true;
        return isShowTime;
    }

    public int getConvTypeInt(ChatMsgEntity entity) {
        int convType = 0;
        if (entity.getConvType() == ConvType.single)
            convType = 1;
        else if (entity.getConvType() == ConvType.group)
            convType = 2;
        else if (entity.getConvType() == ConvType.room)
            convType = 3;
        return convType;
    }

    public ConvType getConvTypeEnum(int type) {
        ConvType convType = ConvType.unknown;
        if (type == 1)
            convType = ConvType.single;
        else if (type == 2)
            convType = ConvType.group;
        else if (type == 3)
            convType = ConvType.room;
        return convType;
    }

    public int getUnreadMsgNum() {
        return unreadMsgNum;
    }

    public void setUnreadMsgNum(int unreadMsgNum) {
        this.unreadMsgNum = unreadMsgNum;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOppositeId() {
        return oppositeId;
    }

    public void setOppositeId(String oppositeId) {
        this.oppositeId = oppositeId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }

    public String getImgThumbnail() {
        return imgThumbnail;
    }

    public void setImgThumbnail(String imgThumbnail) {
        this.imgThumbnail = imgThumbnail;
    }

    public FileEntity getFileEntity() {
        return fileEntity;
    }

    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    public ConvType getConvType() {
        return convType;
    }

    public void setConvType(ConvType convType) {
        this.convType = convType;
    }
}
