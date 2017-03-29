package com.ioyouyun.search.biz;

/**
 * Created by 卫彪 on 2016/11/3.
 */

public interface UsersRequest {

  /**
   * 搜索用户
   * @param uid
   * @param listener
   */
  void searchUserById(long uid, OnUsersListener listener);

  /**
   * 添加好友
   * @param uid
   * @param listener
     */
  void addUsers(String uid, OnUsersListener listener);

}
