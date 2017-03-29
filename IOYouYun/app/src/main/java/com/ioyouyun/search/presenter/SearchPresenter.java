package com.ioyouyun.search.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.search.biz.OnUsersListener;
import com.ioyouyun.search.biz.UsersRequest;
import com.ioyouyun.search.biz.UsersRequestImpl;
import com.ioyouyun.search.view.SearchView;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/10/24.
 */
public class SearchPresenter extends BasePresenter<SearchView> {

    private Activity activity;
    private GroupRequest groupRequest;
    private UsersRequest usersRequest;
    private Handler handler;

    public SearchPresenter(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        groupRequest = new GroupRequestImpl();
        usersRequest = new UsersRequestImpl();
    }

    /**
     * 搜索好友
     * @param uid
     */
    public void searchUser (String uid) {
        showLoading();
        usersRequest.searchUserById(string2Long(uid), new OnUsersListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();
                JSONObject object = ParseJson.parseCommonObject(response);
                final UserInfoEntity entity = ParseJson.parseJson2T(object, UserInfoEntity.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.searchResult(entity);
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    /**
     * 搜索群
     * @param gid
     */
    public void searchGroup(String gid){
        showLoading();
        groupRequest.searchGroupById(string2Long(gid), new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();
                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.searchResult(entity);
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    /**
     * 申请入群
     * @param name
     * @param gid
     */
    public void applyAddGroup(String name, String gid){
        showLoading();
        groupRequest.applyAddGroup(string2Long(gid), name + "申请加入群", new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();
                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView != null)
                            mView.applyAddGroup(result);
                    }
                });
            }

            @Override
            public void onFaild() {
                closeLoading();
            }
        });
    }

    /**
     * 加好友
     * @param uid
     */
    public void addUser(String uid){
        usersRequest.addUsers(uid, new OnUsersListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFaild() {

            }
        });
    }

    private long string2Long(String str){
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
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
