package com.ioyouyun.group.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.AddGroupVerifyPresenter;
import com.ioyouyun.group.view.AddGroupVerifyView;
import com.ioyouyun.utils.GotoActivityUtils;

/**
 * 加群验证Activity
 */
public class AddGroupVerifyActivity extends BaseActivity<AddGroupVerifyView, AddGroupVerifyPresenter> implements AddGroupVerifyView {

    ImageView[] verifyImage = new ImageView[3];
    LinearLayout[] verifyLayout = new LinearLayout[3];
    private Button createBtn;
    private GroupInfoEntity entity;
    private int verify;

    @Override
    protected AddGroupVerifyPresenter initPresenter() {
        return new AddGroupVerifyPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_group_verify;
    }

    @Override
    protected void initView() {
        verifyImage[0] = $findViewById(R.id.iv_allow_anyone);
        verifyImage[1] = $findViewById(R.id.iv_need_verify);
        verifyImage[2] = $findViewById(R.id.iv_not_allow_add);
        verifyLayout[0] = $findViewById(R.id.ll_allow_anyone);
        verifyLayout[1] = $findViewById(R.id.ll_need_verify);
        verifyLayout[2] = $findViewById(R.id.ll_not_allow_add);
        createBtn = $findViewById(R.id.btn_create);
    }

    @Override
    protected void setListener() {
        createBtn.setOnClickListener(this);
        for (int i = 0; i < verifyLayout.length; i++){
            verifyLayout[i].setOnClickListener(this);
        }
    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        entity = bundle.getParcelable(KEY_KEY);
    }

    @Override
    public void widgetClick(View v) {
        if(v == createBtn) {
            entity.setAddGroupVerify(verify);
            presenter.createGroup(entity);
        } else {
            for (int i = 0; i < verifyLayout.length; i++){
                if (v == verifyLayout[i]) {
                    verify = i;
                    verifyImage[i].setVisibility(View.VISIBLE);
                } else {
                    verifyImage[i].setVisibility(View.GONE);
                }
            }

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

    @Override
    public void createSuccess(GroupInfoEntity entity) {
        // 到群聊
        GotoActivityUtils.INSTANSE.transferChatActivity(this, 1, entity.getGid(), entity.getName(), 1, entity.getRole(), null);
        this.finish();
    }
}
