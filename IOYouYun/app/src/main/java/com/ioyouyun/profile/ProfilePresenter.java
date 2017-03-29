package com.ioyouyun.profile;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.profile.biz.OnRequestListener;
import com.ioyouyun.profile.biz.ProfileRequest;
import com.ioyouyun.profile.biz.ProfileRequestImpl;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONObject;

/**
 * Created by luis on 2016/10/19.
 */

public class ProfilePresenter extends BasePresenter<ProfileView> {

  private ProfileRequest request;
  private Handler handler;

  public ProfilePresenter() {
    request = new ProfileRequestImpl();
    handler = new Handler(Looper.getMainLooper());
  }

  public void getUserinfo() {
    mView.showLoading();
    request.getUserinfo(FunctionUtil.uid, new OnRequestListener() {
      @Override
      public void onSuccess(final String response) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            mView.hideLoading();
            JSONObject object = ParseJson.parseCommonObject(response);
            UserInfoEntity entity = ParseJson.parseJson2T(object, UserInfoEntity.class);
            if (entity == null) {
              FunctionUtil.toastMessage("获取个人信息失败，请稍后重试～");
            } else {
              mView.setUserinfo(entity);
            }
          }
        });
      }

      @Override
      public void onFaild() {
        handler.post(new Runnable() {
          @Override
          public void run() {
            mView.hideLoading();
            FunctionUtil.toastMessage("获取个人信息失败，请稍后重试～");
          }
        });
      }
    });
  }
}
