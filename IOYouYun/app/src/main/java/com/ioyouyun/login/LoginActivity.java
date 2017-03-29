package com.ioyouyun.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.login.presenter.LoginPresenter;
import com.ioyouyun.login.view.LoginView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.LoginSharedUtil;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView {

    private Button loginBtn;

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        loginBtn = $findViewById(R.id.btn_login);
    }

    @Override
    protected void setListener() {
        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void widgetClick(View v) {
        presenter.login();
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
        if(YouyunDbManager.getIntance().createRecentContactTable()){
            LoginSharedUtil.getInstance().setLogin(true);
            presenter.init();

            $startActivity(HomeActivity.class);
            finish();
        }

    }

    @Override
    public void loginFail() {
        FunctionUtil.toastMessage("登录失败");
    }

}
