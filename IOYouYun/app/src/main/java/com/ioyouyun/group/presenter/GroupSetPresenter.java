package com.ioyouyun.group.presenter;

import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.biz.GroupRequest;
import com.ioyouyun.group.biz.GroupRequestImpl;
import com.ioyouyun.group.biz.OnGroupListener;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;
import com.ioyouyun.group.view.GroupSetView;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.ParseJson;
import com.ioyouyun.wchat.message.ConvType;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 卫彪 on 2016/11/7.
 */
public class GroupSetPresenter extends BasePresenter<GroupSetView> {

    private Handler handler;
    private GroupRequest request;

    public GroupSetPresenter() {
        handler = new Handler(Looper.getMainLooper());
        request = new GroupRequestImpl();
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
                String settings = null;
                if(entity != null)
                    settings = entity.getSettings();
                JSONObject object = null;
                try {
                    object = new JSONObject(settings);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final GroupSettingEntity settingEntity = ParseJson.parseJson2T(object, GroupSettingEntity.class);

                if (entity != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.setGroupInfo(entity, settingEntity);
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
     * 退群和聊天室
     *
     * @param gid
     * @param convType
     */
    public void exitGroup(final String gid, ConvType convType) {
        if (mView != null)
            mView.showLoading();
        if(ConvType.room == convType){
            request.exitChatRoom(gid, new OnGroupListener() {
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
        } else if(ConvType.group == convType){
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

    }

    /**
     * 解散群(群和聊天室一个接口)
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

    /**
     * 删除本地数据
     *
     * @param uid
     */
    public void clearLocalData(String uid) {
        String name = FunctionUtil.jointTableName(uid);
        List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
        if (YouyunDbManager.getIntance().removeChatImageMsg(name)) {
            for (ChatMsgEntity entity : list) {
                if (ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()) {
                    YouyunDbManager.getIntance().deleteFileInfoById(entity.getMsgId());
                }
            }
            FunctionUtil.toastMessage("清空成功");

            MessageEvent.RefreshChatListEvent event = new MessageEvent.RefreshChatListEvent();
            event.touid = uid;
            EventBus.getDefault().post(event);
        }
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
