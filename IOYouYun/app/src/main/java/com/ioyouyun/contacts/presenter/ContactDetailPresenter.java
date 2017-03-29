package com.ioyouyun.contacts.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.contacts.view.ContactDetailView;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.WeimiInstance;
import com.weimi.media.WMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/7/21.
 */
public class ContactDetailPresenter extends BasePresenter<ContactDetailView> {

    private Activity activity;
    private MyInnerReceiver receiver;
    private String uid;

    public ContactDetailPresenter(Activity activity) {
        this.activity = activity;
        registerReceiver();
    }

    /**
     * 申请房间
     */
    public void applyRoom(String uid) {
        this.uid = uid;
        WeimiInstance.getInstance().conferenceRequestRoom(FunctionUtil.genLocalMsgId());
    }

    public void onDestroy() {
        unregisterReceiver();
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver() {
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.CONFERENCE_REQUEST_ROOM);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        if (receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.v("conference:" + action);
            if (FunctionUtil.CONFERENCE_REQUEST_ROOM.equals(action)) {
                String currentActivity = FunctionUtil.getCurrentActivity();
                if (FunctionUtil.CONFERENCEACTIVITY_PATH.equals(currentActivity)) {
                    return;
                }
                String content = intent.getStringExtra(FunctionUtil.CONTENT);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String inviteGroupId = jsonObject.getString("groupid");
                    JSONObject roomObj = new JSONObject(jsonObject.getString("room"));
                    String inviteRoomId = roomObj.getString("id");
                    String inviteRoomKey = roomObj.getString("key");

                    List<String> list = new ArrayList<>();
                    list.add(uid);
                    WeimiInstance.getInstance().conferenceInviteUsers(list, inviteGroupId, inviteRoomId, inviteRoomKey);

                    boolean result = WMedia.getInstance().callGroup(inviteRoomId, inviteRoomKey);
                    if(result){
                        if(mView != null){
                            mView.intentConference(inviteGroupId, inviteRoomId, inviteRoomKey);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
