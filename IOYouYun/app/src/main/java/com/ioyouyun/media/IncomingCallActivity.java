package com.ioyouyun.media;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.media.presenter.IncommingCallPresenter;
import com.ioyouyun.media.view.IncommingCallView;
import com.ioyouyun.utils.FunctionUtil;
import com.weimi.media.WMedia;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallActivity extends BaseActivity<IncommingCallView, IncommingCallPresenter> implements IncommingCallView {

    @BindView(R.id.tv_uid)
    TextView tvUid;

    private String uid;
    private String nickName;

    @Override
    protected IncommingCallPresenter initPresenter() {
        return new IncommingCallPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_incoming_call;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        if (bundle != null) {
            uid = bundle.getString(FunctionUtil.INCOMINGNAME);
            String nickName = presenter.getLocalNickName(uid);
            if (TextUtils.isEmpty(nickName)) {
                presenter.getNickName(uid);
                tvUid.setText(uid);
            } else {
                this.nickName = nickName;
                tvUid.setText(nickName);
            }
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    /**
     * 接听
     */
    public void answer(String uid) {
        boolean result = WMedia.getInstance().answer();
        if (result) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_UID, uid);
            if (!TextUtils.isEmpty(nickName))
                bundle.putString(KEY_NICKNAME, nickName);
            bundle.putBoolean(KEY_FLAG, true);
            $startActivity(VoIPActivity.class, bundle);
            finish();
        }
    }

    private void decline() {
        presenter.decline();
        finish();
    }

    @OnClick({R.id.btn_answer, R.id.btn_hangup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_answer:
                answer(uid);
                break;
            case R.id.btn_hangup:
                decline();
                break;
        }
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
        tvUid.setText(nickName);
    }
}
