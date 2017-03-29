package com.ioyouyun.profile.biz;

import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.RequestType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luis on 2016/10/19.
 */

public class ProfileRequestImpl implements ProfileRequest {

  @Override
  public void getUserinfo(String uid, final OnRequestListener listener) {
    Map<String, Object> map = new HashMap<>();
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

}
