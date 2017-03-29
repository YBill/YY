package com.ioyouyun.observer;

import com.ioyouyun.chat.model.FileEntity;

/**
 * Created by 卫彪 on 2016/8/2.
 *
 * EventBus事件类
 */
public class MessageEvent {

    /**
     * 下载大图后更新聊天列表数据
     */
    public static class DownloadImageEvent{
        public FileEntity fileEntity;
        public int position;
    }

    /**
     * 刷新聊天列表
     */
    public static class RefreshChatListEvent{
        public String touid;
    }

}
