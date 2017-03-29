package com.ioyouyun.chat.biz;

import com.ioyouyun.receivemsg.ReceiveRunnable;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.protocol.MetaMessageType;
import com.ioyouyun.wchat.util.HttpCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by YWB on 2016/6/5.
 */
public class ChatRequestImpl implements ChatRequest {

    @Override
    public void sendText(String msgId, String tid, String text, ConvType type, byte[] padding, OnChatListener listener) {
        try {
            boolean result = WeimiInstance.getInstance().sendText(msgId, tid, text, type, padding, 120);
            if (listener != null) {
                if (result)
                    listener.onSuccess();
                else
                    listener.onFaild();
            }
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }

    }

    @Override
    public void sendMixedText(String msgId, String tid, String text, ConvType type, byte[] padding, OnChatListener listener) {
        try {
            boolean result = WeimiInstance.getInstance().sendMixedText(msgId, tid, text, type, padding, 120);
            if (listener != null) {
                if (result)
                    listener.onSuccess();
                else
                    listener.onFaild();
            }
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmoji(String msgId, String tid, String text, ConvType type, byte[] padding, OnChatListener listener) {
        try {
            boolean result = WeimiInstance.getInstance().sendEmojiText(msgId, tid, text, type, padding, 120);
            if (listener != null) {
                if (result)
                    listener.onSuccess();
                else
                    listener.onFaild();
            }
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }
    }

    @Override
    public void sendVoiceContinue(String msgId, String tid, String spanId, int spanSequenceNo, boolean endTag, byte[] audioData, ConvType convType, byte[] padding, OnChatListener listener) {
        try {
            boolean result = WeimiInstance.getInstance().sendVoiceContinue(msgId, tid, spanId, spanSequenceNo, endTag, audioData, convType, padding, 120);
            if (listener != null) {
                if (result)
                    listener.onSuccess();
                else
                    listener.onFaild();
            }
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }
    }

    @Override
    public void sendFile(String msgId, String tid, String filePath, String fileName, ConvType convType, MetaMessageType metaType, byte[] padding, byte[] thumbnail, OnChatListener listener) {
        int sliceCount = 0;
        try {
            sliceCount = WeimiInstance.getInstance().sendFile(msgId, tid, filePath, fileName, metaType, null, convType, padding, thumbnail, 600);
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }
        if (sliceCount > 0) {
            if (listener != null)
                listener.onSuccess();

            List<Integer> list = new LinkedList<>();
            for (int i = 1; i <= sliceCount; i++) {
                list.add(i);
            }
            ReceiveRunnable.fileSend.put(msgId, list);
            ReceiveRunnable.fileSendCount.put(msgId, sliceCount);
        } else {
            if (listener != null)
                listener.onFaild();
        }

    }

    @Override
    public void downloadFile(String fileId, String downloadPath, int fileLength, int pieceSize, OnChatListener listener) {
        try {
            boolean result = WeimiInstance.getInstance().downloadFile(fileId, downloadPath, fileLength, null, pieceSize, 120);
            if (listener != null) {
                if (result)
                    listener.onSuccess();
                else
                    listener.onFaild();
            }
        } catch (WChatException e) {
            if (listener != null)
                listener.onFaild();
            e.printStackTrace();
        }
    }

    @Override
    public void getChatHistory(String toUid, long timestamp, int num, ConvType convType, HttpCallback listener) {
        WeimiInstance.getInstance().shortGetHistoryByTime(toUid, timestamp, num, convType, listener, 120);
    }

}
