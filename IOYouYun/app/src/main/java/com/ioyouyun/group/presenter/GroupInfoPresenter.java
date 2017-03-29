package com.ioyouyun.group.presenter;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;
import com.ioyouyun.group.view.GroupInfoView;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.GroupEnum;

/**
 * Created by 卫彪 on 2016/11/21.
 */
public class GroupInfoPresenter extends BasePresenter<GroupInfoView> {

    private Handler handler;
    private GroupRequest request;

    public GroupInfoPresenter() {
        handler = new Handler(Looper.getMainLooper());
        request = new GroupRequestImpl();
    }

    /**
     * 修改群规模
     * @param gid
     * @param level 0：最大50人、1：100人、2：200人、3：500人
     */
    public void modifyGroupSize(String gid, final int level) {
        if (mView != null) {
            mView.showLoading();
        }
        request.modifyGroupLevel(Long.parseLong(gid), level, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final boolean result = ParseJson.parseCommonResult2(response);
                if(mView!= null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.setGroupSizeResult(result, level);
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

    /**
     * 修改群验证方式
     * @param gid
     * @param verify 验证方式 0:不需要审核,no 1:需要验证,yes 2:不允许加入,not_allowed
     */
    public void modifyGroupVerify(String gid, int verify) {
        if (mView != null) {
            mView.showLoading();
        }
        GroupEnum.AddGroupApply apply = null;
        if(0 == verify)
            apply = GroupEnum.AddGroupApply.no;
        else if(1 == verify)
            apply = GroupEnum.AddGroupApply.yes;
        else if(2 == verify)
            apply = GroupEnum.AddGroupApply.not_allowed;
        request.modifyGroupSettingInfo(Long.parseLong(gid), apply, null, -1, -1, -1, -1, null, -1, -1, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

               final GroupSettingEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupSettingEntity.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.setGroupSettings(entity);
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
     * 修改群名称和群简介
     *
     * @param gid
     * @param groupName
     * @param groupIntra
     */
    public void modifyBasicGroupInfo(String gid, String groupName, String groupIntra) {
        if (mView != null) {
            mView.showLoading();
        }
        request.modifyBasicGroupInfo(Long.parseLong(gid), groupName, groupIntra, -1, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.setGroupInfo(entity);
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

    private void closeLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null)
                    mView.hideLoading();
            }
        });
    }


}
