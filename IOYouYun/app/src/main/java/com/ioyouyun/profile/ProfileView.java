package com.ioyouyun.profile;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.login.model.UserInfoEntity;

/**
 * Created by luis on 2016/10/19.
 */

public interface ProfileView extends BaseView{

  void setUserinfo(UserInfoEntity userinfo);

}
