package com.ioyouyun.login.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.login.biz.LoginRequest;
import com.ioyouyun.login.biz.LoginRequestImpl;
import com.ioyouyun.login.biz.OnRequestListener;
import com.ioyouyun.login.view.RegisterView;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/11/3.
 */
public class RegisterPresenter extends BasePresenter<RegisterView> {

    private LoginRequest request;
    private Activity activity;
    private Handler handler;

    public RegisterPresenter(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        request = new LoginRequestImpl();
    }

    public void register(String email, String password, String nickname) {
        showLoading();
        request.regirter(email, password, nickname, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();
                JSONObject obj = ParseJson.parseCommonObject(response);
                if (obj != null) {
                    try {
                        final String uid = obj.getString("uid");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mView != null)
                                    mView.register(uid);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    private void closeLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null)
                    mView.hideLoading();
            }
        });
    }

    private void showLoading(){
        if (mView != null)
            mView.showLoading();
    }


}
