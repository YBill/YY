package com.ioyouyun.group.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.view.CreateGroupView;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.GroupEnum;

/**
 * Created by 卫彪 on 2016/7/5.
 */
public class CreateGroupPresenter extends BasePresenter<CreateGroupView> {

    private GroupRequest request;
    private Handler handler;

    public CreateGroupPresenter() {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 创建聊天室
     * @param entity
     */
    public void createChatRoom(GroupInfoEntity entity) {
        if (mView != null) {
            mView.showLoading();
        }
        request.createGroup(entity.getName(), entity.getIntra(), 3, -1, -1, null, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);

                if (entity != null && !TextUtils.isEmpty(entity.getGid())) {
                    // 创建聊天室成功
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
