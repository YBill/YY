package com.ioyouyun.group.presenter;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.view.AddGroupVerifyView;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.GroupEnum;


/**
 * Created by 卫彪 on 2016/10/20.
 */
public class AddGroupVerifyPresenter extends BasePresenter<AddGroupVerifyView> {

    private GroupRequest request;
    private Handler handler;

    public AddGroupVerifyPresenter() {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    public void createGroup(GroupInfoEntity entity) {
        if (mView != null) {
            mView.showLoading();
        }
        GroupEnum.AddGroupApply apply = null;
        if(entity.getAddGroupVerify() == 0){
            apply = GroupEnum.AddGroupApply.no;
        } else if(entity.getAddGroupVerify() == 1){
            apply = GroupEnum.AddGroupApply.yes;
        }else if(entity.getAddGroupVerify() == 2){
            apply = GroupEnum.AddGroupApply.not_allowed;
        }
        request.createGroup(entity.getName(), entity.getIntra(), 2, 0, entity.getLevel(), apply, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);

                if (entity != null && entity.getGid() != null && !"".equals(entity.getGid())) {
                    // 创建群成功
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null)
                                mView.createSuccess(entity);
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
