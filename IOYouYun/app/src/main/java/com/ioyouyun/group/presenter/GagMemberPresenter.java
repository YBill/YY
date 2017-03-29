package com.ioyouyun.group.presenter;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.view.GagMemberView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.message.ConvType;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 卫彪 on 2016/11/24.
 */
public class GagMemberPresenter extends BasePresenter<GagMemberView> {

    private GroupRequest request;
    private Handler handler;

    public GagMemberPresenter() {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 解禁
     * @param gid
     * @param uids
     * @param convType
     */
    public void relieveBanned(String gid, String uids, ConvType convType){
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            request.setChatRoomGag(gid, uids, 0, false, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    final boolean result = ParseJson.parseCommonResult3(response);
                    if(mView!= null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.operateMemberResult(result);
                            }
                        });
                    }
                }

                @Override
                public void onFaild() {
                    closeLoading();
                }
            });
        }else if(ConvType.group == convType){
            request.setGroupGag(gid, uids, 0, false, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    final boolean result = ParseJson.parseCommonResult3(response);
                    if(mView!= null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.operateMemberResult(result);
                            }
                        });
                    }
                }

                @Override
                public void onFaild() {
                    closeLoading();
                }
            });
        }
    }

    /**
     * 查询禁言成员
     * @param gid
     * @param convType
     */
    public void getGagMember(String gid, ConvType convType){
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            FunctionUtil.toastMessage("聊天室暂不支持查看禁言成员");
            closeLoading();
        }else if(ConvType.group == convType){
            request.getGroupGagUsers(Long.parseLong(gid), new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();
                    JSONArray jsonArray = ParseJson.parseCommonArray(response);
                    if(jsonArray == null)
                        return;
                    final List<String> list = new ArrayList();
                    for (int i = 0; i < jsonArray.length(); i++){
                        list.add(jsonArray.optString(i));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.setListView(list);
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
    }

    private void closeLoading(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(mView != null)
                    mView.hideLoading();
            }
        });
    }

}
