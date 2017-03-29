package com.ioyouyun.loadpage;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.login.LoginAccountActivity;
import com.ioyouyun.login.LoginActivity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.LoginSharedUtil;
import com.ioyouyun.utils.PlatformSharedUtil;
import com.ioyouyun.wchat.util.DebugConfig;

public class SplashActivity extends BaseActivity<SplashView, SplashPresenter> implements SplashView{

    private Handler handler = new Handler();

    @Override
    protected SplashPresenter initPresenter() {
        return new SplashPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {}

    @Override
    protected void setListener() {}

    @Override
    protected void initData() {
        FunctionUtil.isOnlinePlatform = PlatformSharedUtil.getInstance().getPlatform();
        FunctionUtil.loginType = 1;
        final boolean isLogin = LoginSharedUtil.getInstance().getLogin();
        final String account = LoginSharedUtil.getInstance().getAccount();

        if(FunctionUtil.loginType == 1){
            if (isLogin) {
                presenter.login();
            }
        } else if (FunctionUtil.loginType == 2) {
            FunctionUtil.initSDK();
            DebugConfig.DEBUG = true; // 开启debug
            if(isLogin && !TextUtils.isEmpty(account)){
                presenter.login();
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    // 进入首页
                    if(FunctionUtil.loginType == 2 && TextUtils.isEmpty(account)){
                        $startActivity(LoginAccountActivity.class);
                    } else {
                        $startActivity(HomeActivity.class);
                    }
                }else{
                    // 跳到登录页
                    if (FunctionUtil.loginType == 1) {
                        $startActivity(LoginActivity.class);
                    } else if (FunctionUtil.loginType == 2) {
                        $startActivity(LoginAccountActivity.class);
                    }
                }
                finish();
            }
        }, 2000);

    }

    @Override
    public void widgetClick(View v) {}
}
