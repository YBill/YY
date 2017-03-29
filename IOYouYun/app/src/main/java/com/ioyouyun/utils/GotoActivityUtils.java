package com.ioyouyun.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ioyouyun.chat.ChatActivity;

/**
 * Created by Bill on 2016/11/15.
 */

public enum GotoActivityUtils {

    INSTANSE;

    /**
     * 调转到聊天Activity
     * @param context
     * @param intentFlag 返回标记 传1则返回直接从聊天界面回到主页
     * @param toId 用户id或群id或聊天室id
     * @param nickName 昵称
     * @param chatType 聊天类型 0：单聊 1：群组 2：聊天室
     * @param role 用户权限 现在只有聊天室需要用到权限，房主和管理员不退出房间，单聊和群传0即可
     * @param sourse 消息来源 notify:通知 im:聊天
     */
    public void transferChatActivity(Context context, int intentFlag, String toId, String nickName, int chatType, int role, String sourse){
        if(TextUtils.isEmpty(sourse))
            sourse = "im";
        Intent intent = ChatActivity.startActivity(context, intentFlag, toId, nickName, chatType, role, sourse);
        context.startActivity(intent);
    }

}
