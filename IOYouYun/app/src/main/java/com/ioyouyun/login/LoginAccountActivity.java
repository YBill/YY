package com.ioyouyun.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.login.presenter.LoginPresenter;
import com.ioyouyun.login.view.LoginView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.LoginSharedUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginAccountActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView {

    @BindView(R.id.tet_email)
    TextInputEditText tetEmail;
    @BindView(R.id.tet_pwd)
    TextInputEditText tetPwd;

    private String email;
    private String password;

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_account;
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
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setHint("请输入昵称");
        builder.setTitle("登录").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nickname = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(nickname))
                    presenter.setNickName(nickname);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void setNickNameResult(boolean result) {
        if (result) {
            loginSuccess();
        } else {
            FunctionUtil.toastMessage("设置昵称失败");
        }
    }

    @Override
    public void loginSuccess() {
        base64Encode(email, password);

        presenter.init();

        LoginSharedUtil.getInstance().setLogin(true);

        $startActivity(HomeActivity.class);
        finish();
    }

    private void base64Encode(String email, String password){
        String str = email + "@" + password;
        Logger.d("加密账户:" + str);
        String encodeStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        LoginSharedUtil.getInstance().setAccount(encodeStr);
    }

    @Override
    public void loginFail() {
        FunctionUtil.toastMessage("登录失败");
    }

    @OnClick({R.id.btn_login, R.id.tv_forget_pwd, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                email = tetEmail.getText().toString();
                password = tetPwd.getText().toString();
                if(!FunctionUtil.isValidEmail(email)){
                    tetEmail.setError("邮箱格式不正确");
                } else if(TextUtils.isEmpty(password)){
                    tetPwd.setError("密码不能为空");
                }else{
                    presenter.loginAccount(email, password);
                }
                break;
            case R.id.tv_forget_pwd:
                FunctionUtil.toastMessage("仔细想想");
                break;
            case R.id.tv_register:
                $startActivity(RegisterActivity.class);
                break;
        }
    }
}
