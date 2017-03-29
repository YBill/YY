package com.ioyouyun.media.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.login.biz.LoginRequest;
import com.ioyouyun.login.biz.LoginRequestImpl;
import com.ioyouyun.login.biz.OnRequestListener;
import com.ioyouyun.media.view.IncommingCallView;
import com.ioyouyun.utils.ParseJson;
import com.weimi.media.WMedia;

/**
 * Created by 卫彪 on 2016/7/21.
 */
public class IncommingCallPresenter extends BasePresenter<IncommingCallView> {

    private Activity activity;
    private LoginRequest request;
    private Handler handler;

    public IncommingCallPresenter(Activity activity) {
        this.activity = activity;
        request = new LoginRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 挂断
     */
    public void decline() {
        WMedia.getInstance().decline();
    }

    /**
     * 获取昵称
     *
     * @param uid
     */
    public String getLocalNickName(String uid) {
        UserInfoEntity entity = request.getUserInfo(uid);
        if (null != entity && !TextUtils.isEmpty(entity.getNickname())) {
            return entity.getNickname();
        }
        return null;
    }

    /**
     * 获取昵称
     *
     * @param uid
     */
    public void getNickName(final String uid) {
        request.getNickName(uid, new OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                UserInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), UserInfoEntity.class);
                if (entity != null) {
                    if (!TextUtils.isEmpty(entity.getNickname())) {
                        final String nickName = entity.getNickname();
                        UserInfoEntity userInfoEntity = new UserInfoEntity(uid, nickName);
                        request.saveUserInfo(userInfoEntity);

                        if (mView != null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mView.setNickName(nickName);
                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onFaild() {

            }
        });
    }

}
