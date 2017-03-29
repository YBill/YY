package com.ioyouyun.group.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.view.GroupSettingView;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 卫彪 on 2016/7/7.
 */
public class GroupSettingPresenter extends BasePresenter<GroupSettingView> {

    private GroupRequest request;
    private Handler handler;
    private Activity activity;

    public GroupSettingPresenter(Activity activity) {
        request = new GroupRequestImpl();
        handler = new Handler(Looper.getMainLooper());
        this.activity = activity;
    }

    public void clearLocalData(String gid) {
        String name = FunctionUtil.jointTableName(gid);
        List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
        if (YouyunDbManager.getIntance().removeChatImageMsg(name)){
            for (ChatMsgEntity entity : list){
                if(ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()){
                    YouyunDbManager.getIntance().deleteFileInfoById(entity.getMsgId());
                }
            }

            FunctionUtil.toastMessage("清空成功");

            MessageEvent.RefreshChatListEvent event = new MessageEvent.RefreshChatListEvent();
            event.touid = gid;
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 获取群信息
     *
     * @param gid
     */
    public void getGroupInfo(String gid) {
        if (mView != null) {
            mView.showLoading();
        }
        request.getGroupInfo(Long.parseLong(gid), new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final GroupInfoEntity entity = ParseJson.parseJson2T(ParseJson.parseCommonObject(response), GroupInfoEntity.class);
                if (entity != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.setGroupInfo(entity);
                            }
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
     * 退群
     *
     * @param gid
     */
    public void exitGroup(final String gid) {
        if (mView != null)
            mView.showLoading();
        request.exitGroup(Long.parseLong(gid), new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.exitGroup(gid, result);
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.hideLoading();
                            mView.exitGroup(gid, false);
                        }
                    }
                });
            }
        });
    }

    /**
     * 解散群
     *
     * @param gid
     */
    public void deleteGroup(final String gid) {
        if (mView != null)
            mView.showLoading();
        request.deleteGroup(gid, new OnGroupListener() {
            @Override
            public void onSuccess(String response) {
                closeLoading();

                final boolean result = ParseJson.parseCommonResult2(response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.delGroup(gid, result);
                        }
                    }
                });
            }

            @Override
            public void onFaild() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.hideLoading();
                            mView.delGroup(gid, false);
                        }
                    }
                });
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
