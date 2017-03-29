package com.ioyouyun.search.biz;

import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.util.HttpCallback;

import java.util.List;

/**
 * Created by 卫彪 on 2016/11/3.
 */

public class UsersRequestImpl implements UsersRequest {

    @Override
    public void searchUserById(long uid, final OnUsersListener listener) {
        WeimiInstance.getInstance().searchUserById(uid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("searchUserById success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("searchUserById error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void addUsers(String uid, OnUsersListener listener) {

    }

}
