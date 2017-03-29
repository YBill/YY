package com.ioyouyun.chat.view;

import com.ioyouyun.chat.model.ChatMsgEntity;

import java.util.List;


/**
 * Created by YWB on 2016/6/5.
 */
public interface ChatView {

    /**
     * listview显示数据
     *
     * @param list
     */
    void showChatMsgList(List<ChatMsgEntity> list);

    /**
     * 设置ListView光标位置
     *
     * @param position
     */
    void setChatSelection(int position);

    /**
     * 清空聊天输入框
     */
    void clearChatContent();

    /**
     * 加载完成
     */
    void onCompleteLoad();

    /**
     * 进入聊天室结果
     * @param result
     */
    void enterChatRoom(boolean result);

    /**
     * 退出聊天室
     * @param result
     */
    void exitChatRoom(boolean result);
}
