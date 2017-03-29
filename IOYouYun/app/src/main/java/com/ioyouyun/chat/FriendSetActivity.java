package com.ioyouyun.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.presenter.FriendSetPresenter;
import com.ioyouyun.chat.view.FriendSetView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendSetActivity extends BaseActivity<FriendSetView, FriendSetPresenter> implements FriendSetView {

    @BindView(R.id.tv_name)
    TextView tvName;

    private String uid;
    private String nickName;

    @Override
    protected FriendSetPresenter initPresenter() {
        return new FriendSetPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_set;
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
        if(bundle != null) {
            uid = bundle.getString(KEY_UID);
            nickName = bundle.getString(KEY_NICKNAME);
        }
        tvName.setText(nickName);
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick({R.id.iv_add, R.id.tv_clear_chat, R.id.btn_del_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                break;
            case R.id.tv_clear_chat:
                presenter.clearLocalData(uid);
                break;
            case R.id.btn_del_friend:
                break;
        }
    }
}
