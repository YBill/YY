package com.ioyouyun.media;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.ParentActivity;
import com.ioyouyun.R;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.login.biz.LoginRequest;
import com.ioyouyun.login.biz.LoginRequestImpl;
import com.weimi.media.WMedia;

/**
 * Created by 卫彪 on 2016/7/22.
 */
public class BeInviteActivity extends ParentActivity {

    private TextView tvInviteUser;
    private View acceptBtn;
    private View refuseBtn;
    private String invitedFrom;
    private String invitedRoomId;
    private String invitedRoomKey;
    private String invitedGroupId;

    private LoginRequest request;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_beinvite;
    }

    @Override
    protected void initView() {
        tvInviteUser = $findViewById(R.id.tv_invite_user);
        acceptBtn = $findViewById(R.id.tv_accept);
        refuseBtn = $findViewById(R.id.tv_refuse);
    }

    @Override
    protected void setListener() {
        acceptBtn.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        request = new LoginRequestImpl();

        Bundle bundle = $getIntentExtra();
        invitedFrom = bundle.getString("invitedFrom");
        invitedRoomId = bundle.getString(KEY_ROOMID);
        invitedRoomKey = bundle.getString(KEY_KEY);
        invitedGroupId = bundle.getString(KEY_GID);

        String msg = String.format(getResources().getString(R.string.conference_invite_msg), getLocalNickName(invitedFrom));
        tvInviteUser.setText(msg);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.tv_refuse:
                finish();
                break;
            case R.id.tv_accept:
                WMedia.getInstance().callGroup(invitedRoomId, invitedRoomKey);

                Bundle bundle = new Bundle();
                bundle.putString(KEY_GID, invitedGroupId);
                bundle.putString(KEY_ROOMID, invitedRoomId);
                bundle.putString(KEY_KEY, invitedRoomKey);
                $startActivity(ConferenceActivity.class, bundle);
                finish();
                break;
        }
    }

    /**
     * 获取昵称
     *
     * @param uid
     */
    private String getLocalNickName(String uid) {
        String nickName;
        UserInfoEntity entity = request.getUserInfo(uid);
        if (null == entity || TextUtils.isEmpty(entity.getNickname())) {
            nickName = uid;
        } else {
            nickName = entity.getNickname();
        }
        return nickName;
    }

}
