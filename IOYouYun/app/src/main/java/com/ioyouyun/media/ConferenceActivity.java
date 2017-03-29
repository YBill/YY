package com.ioyouyun.media;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.InviteMemberActivity;
import com.ioyouyun.media.adapter.MemberListAdapter;
import com.ioyouyun.media.model.MemberEntity;
import com.ioyouyun.media.presenter.ConferencePresenter;
import com.ioyouyun.media.view.ConferenceView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConferenceActivity extends BaseActivity<ConferenceView, ConferencePresenter> implements ConferenceView {

    @BindView(R.id.tv_user_info)
    TextView tvUserInfo;
    @BindView(R.id.tv_call_time)
    TextView tvCallTime;
    @BindView(R.id.iv_calling_mute)
    ImageView ivCallingMute;
    @BindView(R.id.iv_calling_speaker)
    ImageView ivCallingSpeaker;
    @BindView(R.id.tv_userlist_des)
    TextView tvUserlistDes;
    @BindView(R.id.lv_user_member)
    ListView lvUserMember;

    private static final int REQUEST_CODE_INVITE_MEMBER = 1001;
    private String invitedRoomId;
    private String invitedRoomKey;
    private String invitedGroupId;
    private MemberListAdapter adapter;

    @Override
    protected ConferencePresenter initPresenter() {
        return new ConferencePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conference;
    }

    @Override
    protected void $setToolBar() {
        Toolbar toolbar = $findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        $setToolBar();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        if (bundle == null) {
            finish();
        }
        invitedRoomId = bundle.getString(KEY_ROOMID);
        invitedRoomKey = bundle.getString(KEY_KEY);
        invitedGroupId = bundle.getString(KEY_GID);
        Logger.d("Bill", "con:::" + "invitedRoomId:" + invitedRoomId + "|invitedRoomKey:" + invitedRoomKey + "|invitedGroupId:" + invitedGroupId);

        setListNumber(0);

        String format2 = getResources().getString(R.string.user_info);
        String userInfo = String.format(format2, FunctionUtil.nickname, FunctionUtil.uid, invitedRoomId);
        tvUserInfo.setText(userInfo);

        adapter = new MemberListAdapter(this);
        lvUserMember.setAdapter(adapter);
    }

    @Override
    public void widgetClick(View v) {

    }

    private void setListNumber(int number) {
        String format = getResources().getString(R.string.user_list);
        String member = String.format(format, number);
        tvUserlistDes.setText(member);
    }

    private void refreshAdapter(List<MemberEntity> list) {
        adapter.setMemberList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnClick({R.id.tv_hangup, R.id.tv_invite_member, R.id.iv_calling_mute, R.id.iv_calling_speaker, R.id.tv_refresh_member})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_hangup:
                presenter.hangUp();
                finish();
                break;
            case R.id.tv_invite_member:
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_FLAG, 2);
                bundle.putString(KEY_ROOMID, invitedRoomId);
                bundle.putString(KEY_KEY, invitedRoomKey);
                bundle.putString(KEY_GID, invitedGroupId);
                $startActivityForResult(InviteMemberActivity.class, bundle, REQUEST_CODE_INVITE_MEMBER);
                break;
            case R.id.tv_refresh_member:
                getRoomList();
                break;
            case R.id.iv_calling_mute: // 麦
                presenter.toggleMicro();
                break;
            case R.id.iv_calling_speaker: // 听筒(true开启扬声器)
                presenter.toggleSpeaker();
                break;
        }
    }

    @Override
    public void setCallTime(String time) {
        tvCallTime.setText(time);
    }

    @Override
    public void setListView(List<String> list) {
        List<MemberEntity> memberEntityList = new ArrayList<>();
        for (String uid : list) {
            String nickName = presenter.getLocalNickName(uid);
            if (TextUtils.isEmpty(nickName)) {
                nickName = uid;
                presenter.getNickName(uid);
            }
            MemberEntity entity = new MemberEntity(uid, nickName);
            memberEntityList.add(entity);
        }

        setListNumber(memberEntityList.size());
        refreshAdapter(memberEntityList);
    }

    @Override
    public void getRoomList() {
        presenter.refreshMember(invitedRoomId, invitedGroupId);
    }

    @Override
    public void toggleSpeaker(boolean isSpeaker) {
        if (isSpeaker) {
            ivCallingSpeaker.setImageResource(R.drawable.speaker);
        } else {
            ivCallingSpeaker.setImageResource(R.drawable.nospeaker);
        }
    }

    @Override
    public void toggleMicro(boolean isMicMut) {
        if (isMicMut) {
            ivCallingMute.setImageResource(R.drawable.mute);
        } else {
            ivCallingMute.setImageResource(R.drawable.nomute);
        }
    }

    @Override
    public void setNickName(String uid, String nickName) {
        final List<MemberEntity> list = adapter.getMemberList();
        int index = -1;
        boolean isHave = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).userId.equals(uid)) {
                isHave = true;
                index = i;
                break;
            }
        }
        if (isHave && index > -1) {
            list.get(index).nickName = nickName;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshAdapter(list);
                }
            });
        }

    }
}
