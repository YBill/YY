package com.ioyouyun.login.view;

/**
 * Created by 卫彪 on 2016/8/11.
 */
public interface LoginView {

    /**
     * 登录成功
     */
    void loginSuccess();

    /**
     * 登录失败
     */
    void loginFail();

    /**
     * show Dialog
     */
    void showDialog();

    /**
     * 设置昵称结果
     * @param result
     */
    void setNickNameResult(boolean result);
}
