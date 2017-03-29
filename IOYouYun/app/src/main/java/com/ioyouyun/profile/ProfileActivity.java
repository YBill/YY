package com.ioyouyun.profile;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.utils.LoginSharedUtil;
import com.ioyouyun.wchat.WeimiInstance;
import com.melink.bqmmsdk.ui.store.EmojiPackageList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luis on 2016/10/19.
 */

public class ProfileActivity extends BaseActivity<ProfileView, ProfilePresenter> implements ProfileView {

    @BindView(R.id.iv_profile_avatar)
    ImageView ivAvatar;
    @BindView(R.id.ll_profile_avatar)
    LinearLayout llAvatar;
    @BindView(R.id.tv_profile_nickname)
    TextView tvNickname;
    @BindView(R.id.ll_profile_nickname)
    LinearLayout llNickname;
    @BindView(R.id.tv_profile_gender)
    TextView tvGender;
    @BindView(R.id.ll_profile_gender)
    LinearLayout llGender;
    @BindView(R.id.tv_profile_email)
    TextView tvEmail;
    @BindView(R.id.ll_profile_email)
    LinearLayout llEmail;
    @BindView(R.id.tv_profile_friend_validation)
    TextView tvFriendValidation;
    @BindView(R.id.ll_profile_friend_validation)
    LinearLayout llFriendValidation;
    @BindView(R.id.ll_profile_emotion_mall)
    LinearLayout llEmotionMall;
    @BindView(R.id.tv_profile_logout)
    TextView tvLogout;

    @Override
    protected ProfilePresenter initPresenter() {
        return new ProfilePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {
        tvLogout.setOnClickListener(this);
        llEmotionMall.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        presenter.getUserinfo();
    }

    @Override
    public void widgetClick(View v) {
        if (v == tvLogout) {
            WeimiInstance.getInstance().logout();
            LoginSharedUtil.getInstance().setLogin(false);
        } else if (v == llEmotionMall) {
            $startActivity(EmojiPackageList.class);
        }
    }

    @Override
    public void setUserinfo(UserInfoEntity userinfo) {
        if (userinfo == null) {
            return;
        }
        if (!TextUtils.isEmpty(userinfo.getNickname())) {
            tvNickname.setText(userinfo.getNickname());
        }
        if (!TextUtils.isEmpty(userinfo.getGender())) {
            tvGender.setText(userinfo.getGender());
        }
        if (!TextUtils.isEmpty(userinfo.getId())) {
            tvEmail.setText(userinfo.getId() + "@qq.com");
        }
    }

    @Override
    public void showLoading() {
        loddingDialog.showProgress();
    }

    @Override
    public void hideLoading() {
        loddingDialog.cancleProgress();
    }

}
