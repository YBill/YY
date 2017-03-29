package com.ioyouyun.group.activity;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.presenter.ApplyAddGroupPresenter;
import com.ioyouyun.group.view.ApplyAddGroupView;
import com.ioyouyun.utils.FunctionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyAddGroupActivity extends BaseActivity<ApplyAddGroupView, ApplyAddGroupPresenter> implements ApplyAddGroupView {

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.et_search_id)
    EditText etSearchId;
    private String groupId;

    @Override
    protected ApplyAddGroupPresenter initPresenter() {
        return new ApplyAddGroupPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_add_group;
    }

    @Override
    protected void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        getIntentExtra();
    }

    @Override
    protected void setListener() {

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null)
            groupId = intent.getStringExtra("gid");
    }

    @Override
    protected void initData() {
        tvTopTitle.setText(getResources().getString(R.string.add_group_apply));
        tvTopTitle.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
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

    @Override
    public void addGroupResult(boolean result) {
        if (result) {
            FunctionUtil.toastMessage("申请成功");
            setResult(RESULT_OK);
            finish();
        } else
            FunctionUtil.toastMessage("申请失败");
    }

    @OnClick({R.id.btn_left, R.id.tv_confirm_apply})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.tv_confirm_apply:
                String text = etSearchId.getText().toString().trim();
                presenter.applyAddGroup(groupId, text);
                break;
        }
    }
}
