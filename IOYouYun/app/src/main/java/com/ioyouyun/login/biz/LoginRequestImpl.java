package com.ioyouyun.login.biz;

import android.app.Activity;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.RequestType;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.WChatException;
import com.weimi.media.WMedia;

import java.util.HashMap;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class LoginRequestImpl implements LoginRequest {

    @Override
    public void login(final Activity activity, final OnRequestListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthResultData authResultData;
                    if (FunctionUtil.isOnlinePlatform) {
                        authResultData = WeimiInstance.getInstance().registerApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                FunctionUtil.CLIENT_ID,
                                FunctionUtil.SECRET,
                                60);
                    } else {
                        authResultData = WeimiInstance.getInstance().testRegisterApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                FunctionUtil.CLIENT_ID_TEST,
                                FunctionUtil.SECRET_TEST,
                                60);
                    }
                    Logger.d(authResultData.userInfo);
                    if (authResultData.success)
                        listener.onSuccess(authResultData.userInfo);
                    else
                        listener.onFaild();
                } catch (WChatException e) {
                    listener.onFaild();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void loginAccount(final String username, final String password, final boolean isBackground, final OnRequestListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthResultData authResultData = WeimiInstance.getInstance().youyunAuthUser(username, password, isBackground, 60);
                    Logger.d(authResultData.userInfo);
                    if (authResultData.success)
                        listener.onSuccess(authResultData.userInfo);
                    else
                        listener.onFaild();
                } catch (WChatException e) {
                    listener.onFaild();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void regirter(String email, String password, String nickname, final OnRequestListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", email);
        map.put("password", password);
        FunctionUtil.shortConnectRequest("/register/by_username", FunctionUtil.combineParamers(map), RequestType.POST, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public void setNickName(String nickName, final OnRequestListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", nickName);
        FunctionUtil.shortConnectRequest("/users/update", FunctionUtil.combineParamers(map), RequestType.POST, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public void getNickName(String uid, final OnRequestListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        FunctionUtil.shortConnectRequest("/users/show", FunctionUtil.combineParamers(map), RequestType.GET, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public UserInfoEntity getUserInfo(String uid) {
        return YouyunDbManager.getIntance().getUserInfo(uid);
    }

    @Override
    public void saveUserInfo(UserInfoEntity entity) {
        if (entity != null)
            YouyunDbManager.getIntance().insertUserInfo(entity);
    }


    @Override
    public boolean initMediaSDK(Activity activity) {
        try {
            WMedia.getInstance().initWMediaSDK(activity.getApplicationContext(), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUid() {
        return WeimiInstance.getInstance().getUID();
    }

}
