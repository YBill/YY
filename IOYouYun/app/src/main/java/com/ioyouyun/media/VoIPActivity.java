package com.ioyouyun.media;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.media.presenter.VoIPPresenter;
import com.ioyouyun.media.view.VoIPView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoIPActivity extends BaseActivity<VoIPView, VoIPPresenter> implements VoIPView {

    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.iv_speaker)
    ImageView ivSpeaker;

    private boolean isReceive; // true:接电话 false:打电话
    private String uid;
    private String nickName;

    @Override
    protected VoIPPresenter initPresenter() {
        return new VoIPPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vo_ip;
    }

    @Override
    protected void $setToolBar() {
        Toolbar toolbar = $findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUpVoIP();
            }
        });
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
        uid = bundle.getString(KEY_UID);
        isReceive = bundle.getBoolean(KEY_FLAG);
        nickName = bundle.getString(KEY_NICKNAME);
        if (TextUtils.isEmpty(nickName))
            nickName = presenter.getLocalNickName(uid);

        tvUid.setText(uid);
        tvNickname.setText(nickName);

        if (isReceive) {
            presenter.startTime();
        } else {
            setTipsText("等待对方接听，请稍后...");
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    private void hangUpVoIP() {
        presenter.hangUp();
        finish();
    }

    @OnClick({R.id.tv_hangup, R.id.iv_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_hangup:
                hangUpVoIP();
                break;
            case R.id.iv_speaker:
                presenter.toggleSpeaker();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    public void setTipsText(String time) {
        tvTips.setText(time);
    }

    @Override
    public void toggleSpeaker(boolean isSpeaker) {
        if (isSpeaker) {
            ivSpeaker.setImageResource(R.drawable.speaker_on);
        } else {
            ivSpeaker.setImageResource(R.drawable.speaker_off);
        }
    }

    @Override
    public void toggleMicro(boolean isMicMut) {

    }
}
