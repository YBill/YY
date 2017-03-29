package com.ioyouyun.login;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.login.presenter.RegisterPresenter;
import com.ioyouyun.login.view.RegisterView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity<RegisterView, RegisterPresenter> implements RegisterView {


    @BindView(R.id.tet_nickname)
    TextInputEditText tetNickname;
    @BindView(R.id.tet_email)
    TextInputEditText tetEmail;
    @BindView(R.id.tet_pwd)
    TextInputEditText tetPwd;
    @BindView(R.id.tet_pwd_again)
    TextInputEditText tetPwdAgain;

    @Override
    protected RegisterPresenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void showLoading() {
        loddingDialog.showProgress();
    }

    @Override
    public void hideLoading() {
        loddingDialog.cancleProgress();
    }

    @OnClick({R.id.btn_register, R.id.tv_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                String nickName = tetNickname.getText().toString();
                String email = tetEmail.getText().toString();
                String password = tetPwd.getText().toString();
                String pwdAgain = tetPwdAgain.getText().toString();
                if (!FunctionUtil.isValidEmail(email))
                    tetEmail.setError("邮箱格式不正确");
                else if (password.length() < 6)
                    tetPwd.setError("密码不少于6个字符");
                else if (!TextUtils.equals(password, pwdAgain))
                    tetPwdAgain.setError("两次密码不一样");
                else
                    presenter.register(email, password, nickName);
                break;
            case R.id.tv_login:
                finish();
                break;
        }
    }

    @Override
    public void register(String uid) {
        if (TextUtils.isEmpty(uid))
            FunctionUtil.toastMessage("注册失败");
        else {
            Logger.d("注册成功,uid:" + uid);
            finish();
        }
    }
}
