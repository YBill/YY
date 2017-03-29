package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.ChatRoomInfoEntity;
import com.ioyouyun.group.model.GroupMemberEntity;
import com.ioyouyun.group.view.GroupMemberView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.message.ConvType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/7/8.
 */
public class GroupMemberPresenter extends BasePresenter<GroupMemberView> {

    private GroupRequest request;
    private Handler handler;
    private Activity activity;
    private List<GroupMemberEntity> groupMemberEntityList = new ArrayList<>();

    public GroupMemberPresenter(Activity activity) {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
        this.activity = activity;
    }

    /**
     * 踢人
     * @param gid
     * @param uids
     * @param convType
     */
    public void delMember(String gid, String uids, ConvType convType){
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            FunctionUtil.toastMessage("暂不支持此功能");
        }else if(ConvType.group == convType){
            request.kickGroupUsers(Long.parseLong(gid), uids, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    final boolean result = ParseJson.parseCommonResult(response);
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
     * 禁言解禁
     * @param gid
     * @param uids
     * @param convType
     */
    public void bannedSpeak(String gid, String uids, long time, boolean status, ConvType convType){
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            request.setChatRoomGag(gid, uids, time, status, new OnGroupListener() {
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
            request.setGroupGag(gid, uids, time, status, new OnGroupListener() {
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
     * 群主转让
     * @param gid
     * @param uid
     * @param convType
     */
    public void groupManagerTransfer(String gid, String uid, ConvType convType){
        operateMember(gid, uid, 4, convType);
    }

    /**
     * 升级管理员
     * @param gid
     * @param uid
     * @param convType
     */
    public void upgradeAdministrato(String gid, String uid, ConvType convType){
        operateMember(gid, uid, 3, convType);
    }

    /**
     * 解除管理员
     * @param gid
     * @param uid
     */
    public void relieveAdministrato(String gid, String uid, ConvType convType){
        operateMember(gid, uid, 1, convType);
    }

    /**
     * 操作成员身份
     * @param gid
     * @param uid
     * @param role
     * @param convType
     */
    private void operateMember(String gid, String uid, int role, ConvType convType){
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            request.modifyChatRoomMemberRole(gid, uid, ""+role, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    final boolean result = ParseJson.parseCommonResult2(response);
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
            request.modifyGroupMemberRole(Long.parseLong(gid), uid, role, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    final boolean result = ParseJson.parseCommonResult2(response);
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
     * 获取群成员
     *
     * @param gid
     * @param convType
     */
    public void getGroupMember(String gid, ConvType convType) {
        if(mView != null){
            mView.showLoading();
        }
        if(ConvType.room == convType){
            request.getChatRoomMembers(gid, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    JSONObject obj = ParseJson.parseCommonObject(response);
                    JSONArray array = null;
                    if(obj != null){
                        array = obj.optJSONArray("users");
                    }
                    groupMemberEntityList.clear();
                    List list = ParseJson.parseJson2ListT(array, ChatRoomInfoEntity.class);
                    if(list != null)
                        groupMemberEntityList.addAll(list);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.setListView(groupMemberEntityList);
                            }
                        }
                    });
                }

                @Override
                public void onFaild() {
                    closeLoading();
                }
            });
        }else if(ConvType.group == convType){
            request.getGroupMembers(Long.parseLong(gid), -1, -1, new OnGroupListener() {
                @Override
                public void onSuccess(String response) {
                    closeLoading();

                    JSONObject obj = ParseJson.parseCommonObject(response);
                    JSONArray array = null;
                    if(obj != null){
                        array = obj.optJSONArray("roles");
                    }
                    groupMemberEntityList.clear();
                    List list = ParseJson.parseJson2ListT(array, GroupMemberEntity.class);
                    if(list != null)
                        groupMemberEntityList.addAll(list);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.setListView(groupMemberEntityList);
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
