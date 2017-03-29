package com.ioyouyun.login.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.login.biz.LoginRequest;
import com.ioyouyun.login.biz.LoginRequestImpl;
import com.ioyouyun.login.biz.OnRequestListener;
import com.ioyouyun.login.view.LoginView;
import com.ioyouyun.receivemsg.PushUtils;
import com.ioyouyun.receivemsg.ReceiveRunnable;
import com.ioyouyun.settings.biz.OnSettingListener;
import com.ioyouyun.settings.biz.SettingRequest;
import com.ioyouyun.settings.biz.SettingRequestImpl;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.WeimiInstance;

import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/8/11.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private LoginRequest request;
    private Activity activity;
    private Handler handler;
    private SettingRequest settingRequest;

    public LoginPresenter(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        request = new LoginRequestImpl();
        settingRequest = new SettingRequestImpl();
    }

    /**
     * 设置用户昵称
     */
    public void setNickName(String nickName) {
        request.setNickName(nickName, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject object = ParseJson.parseCommonObject(response);
                UserInfoEntity entity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                if (entity == null || TextUtils.isEmpty(entity.getNickname())) {
                    setNickNameResult(false);
                } else {
                    FunctionUtil.nickname = entity.getNickname();
                    UserInfoEntity userInfoEntity = new UserInfoEntity();
                    userInfoEntity.setId(FunctionUtil.uid);
                    userInfoEntity.setNickname(FunctionUtil.nickname);
                    request.saveUserInfo(userInfoEntity);
                    setNickNameResult(true);
                }
            }

            @Override
            public void onFaild() {
                setNickNameResult(false);
            }
        });
    }

    /**
     * 帐号登录
     * @param email
     * @param pwd
     */
    public void loginAccount(String email, String pwd){
        request.loginAccount(email, pwd, false, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        WeimiInstance.getInstance().initBqmmSDK(activity.getApplicationContext());
                    }
                });
                UserInfoEntity userInfoEntity = null;
                try {
                    Logger.v(response);
                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        userInfoEntity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userInfoEntity == null || TextUtils.isEmpty(userInfoEntity.getId()) || TextUtils.isEmpty(userInfoEntity.getNickname())) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FunctionUtil.toastMessage("未获取到用户信息,请重新登录");
                        }
                    });
                    return;
                }
                FunctionUtil.uid = userInfoEntity.getId();
                if (FunctionUtil.uid.equals(userInfoEntity.getNickname())) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.showDialog();
                        }
                    });
                } else {
                    FunctionUtil.nickname = userInfoEntity.getNickname();
                    if (request.getUserInfo(FunctionUtil.uid) == null) {
                        UserInfoEntity entity = new UserInfoEntity();
                        entity.setId(FunctionUtil.uid);
                        entity.setNickname(FunctionUtil.nickname);
                        request.saveUserInfo(entity);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.loginSuccess();
                        }
                    });
                }
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null)
                            mView.loginFail();
                    }
                });
            }
        });
    }

    /**
     * 登录
     */
    public void login() {
        request.login(activity, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        WeimiInstance.getInstance().initBqmmSDK(activity.getApplicationContext());
                    }
                });
                UserInfoEntity userInfoEntity = null;
                try {
                    Logger.v(response);
                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        userInfoEntity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userInfoEntity == null || TextUtils.isEmpty(userInfoEntity.getId()) || TextUtils.isEmpty(userInfoEntity.getNickname())) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FunctionUtil.toastMessage("未获取到用户信息,请重新登录");
                        }
                    });
                    return;
                }
                FunctionUtil.uid = userInfoEntity.getId();
                if (FunctionUtil.uid.equals(userInfoEntity.getNickname())) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.showDialog();
                        }
                    });
                } else {
                    FunctionUtil.nickname = userInfoEntity.getNickname();
                    if (request.getUserInfo(FunctionUtil.uid) == null) {
                        UserInfoEntity entity = new UserInfoEntity();
                        entity.setId(FunctionUtil.uid);
                        entity.setNickname(FunctionUtil.nickname);
                        request.saveUserInfo(entity);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.loginSuccess();
                        }
                    });
                }

            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null)
                            mView.loginFail();
                    }
                });
            }
        });
    }

    /**
     * 初始化Push和Media SDK
     */
    public void init() {
        initMediaSDK();
        initReceive();
        startPush();
    }

    private void initReceive() {
        ReceiveRunnable receiveRunnable = new ReceiveRunnable(FunctionUtil.getmAppContext());
        Thread msgHandler = new Thread(receiveRunnable);
        msgHandler.start();
    }

    private void setNickNameResult(final boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null)
                    mView.setNickNameResult(result);
            }
        });
    }

    private void initMediaSDK() {
        boolean result = request.initMediaSDK(activity);
        if (!result) {
            FunctionUtil.toastMessage("初始化Media_SDK失败");
        }
    }

    private void startPush() {
        settingRequest.pushCreate(null, null, new OnSettingListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FunctionUtil.toastMessage("初始化PUSH失败");
                    }
                });
            }
        });
        PushUtils.startPush();
    }

}
