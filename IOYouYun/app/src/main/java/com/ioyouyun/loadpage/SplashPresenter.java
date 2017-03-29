package com.ioyouyun.loadpage;

import android.app.Activity;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.utils.FunctionUtil;

/**
 * Created by 卫彪 on 2016/10/11.
 */

public class SplashPresenter extends BasePresenter<SplashView> {

    private Activity activity;

    public SplashPresenter(Activity activity){
        this.activity = activity;
    }

    public void login() {
        FunctionUtil.autoLogin(activity, null);
    }

}
