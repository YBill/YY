package com.ioyouyun.login.biz;

import android.app.Activity;

import com.ioyouyun.login.model.UserInfoEntity;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public interface LoginRequest {

    /**
     * 登陆
     *
     * @param activity
     * @param listener
     */
    void login(Activity activity, OnRequestListener listener);

    /**
     * 帐号登录
     * @param username 用户名
     * @param password 密码
     * @param isBackground true: SDK重连 false：不重连
     * @param listener
     */
    void loginAccount(String username, String password, boolean isBackground, OnRequestListener listener);

    /**
     * 注册
     * @param email
     * @param password
     * @param nickname
     * @param listener
     */
    void regirter(String email, String password, String nickname, OnRequestListener listener);

    /**
     * 设置用户信息
     * @param nickName
     * @param listener
     */
    void setNickName(String nickName, OnRequestListener listener);

    /**
     * 获取用户昵称
     * @param uid
     * @param listener
     */
    void getNickName(String uid, OnRequestListener listener);

    /**
     * 获取Sqlite里面的用户信息
     * @param uid
     */
    UserInfoEntity getUserInfo(String uid);

    /**
     * 保存uid到Sqlite
     *
     * @param entity
     */
    void saveUserInfo(UserInfoEntity entity);

    /**
     * 初始化Media SDK
     *
     * @param activity
     */
    boolean initMediaSDK(Activity activity);

    /**
     * 获取用户Id
     *
     * @return
     */
    String getUid();

}
