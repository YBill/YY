package com.ioyouyun.profile.biz;

/**
 * Created by luis on 2016/10/19.
 */

public interface ProfileRequest {

  void getUserinfo(String uid, OnRequestListener listener);

}
