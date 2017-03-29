package com.ioyouyun;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.receivemsg.ReceiveRunnable;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.melink.bqmmsdk.sdk.BQMM;

import io.fabric.sdk.android.Fabric;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class YouyunApplication extends Application implements ForegroundCallbacks.Listener{

    public static YouyunApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Fabric.with(this, new Crashlytics());
        FunctionUtil.init(application);
//        initReceive();
        BroadCastCenter.getInstance().init(getApplicationContext());
        ForegroundCallbacks.get(this).addListener(this);
        Logger.isDebug = BuildConfig.LOG_DEBUG;

        BQMM.getInstance().initConfig(this, "", "");
    }

    private void initReceive(){
        ReceiveRunnable receiveRunnable = new ReceiveRunnable(getApplicationContext());
        Thread msgHandler = new Thread(receiveRunnable);
        msgHandler.start();
    }

    @Override
    public void onBecameForeground() {
        FunctionUtil.clearNotify();
    }

    @Override
    public void onBecameBackground() {

    }
}
