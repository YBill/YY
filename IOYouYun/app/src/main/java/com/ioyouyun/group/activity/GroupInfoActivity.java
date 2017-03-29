package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;
import com.ioyouyun.group.presenter.GroupInfoPresenter;
import com.ioyouyun.group.view.GroupInfoView;
import com.ioyouyun.utils.FunctionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改群名称
 * <p>
 * create on 2016/11/21 by Bill
 */
public class GroupInfoActivity extends BaseActivity<GroupInfoView, GroupInfoPresenter> implements GroupInfoView {

    @BindView(R.id.btn_right)
    TextView btnRight;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.tie_intra)
    TextInputEditText tieIntra;
    @BindView(R.id.til_intra)
    TextInputLayout tilIntra;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.llc_verify)
    LinearLayoutCompat llcVerify;
    ImageView[] verifyImage = new ImageView[3];
    LinearLayout[] verifyLayout = new LinearLayout[3];
    private int verify = 0;

    @BindView(R.id.ll_size_50)
    LinearLayout llSize50;
    @BindView(R.id.tv_size_100)
    TextView tvSize100;
    @BindView(R.id.ll_size_100)
    LinearLayout llSize100;
    @BindView(R.id.tv_size_200)
    TextView tvSize200;
    @BindView(R.id.ll_size_200)
    LinearLayout llSize200;
    @BindView(R.id.tv_size_500)
    TextView tvSize500;
    @BindView(R.id.ll_size_500)
    LinearLayout llSize500;
    @BindView(R.id.llc_size)
    LinearLayoutCompat llcSize;

    private String id;
    private int role; // 1: 群 2：聊天室
    private int flag; // 1: 修改群名称 2：修改群简介; 3：加群验证方式 4:群规模
    private int level; // 群规模

    @Override
    protected GroupInfoPresenter initPresenter() {
        return new GroupInfoPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_info;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        verifyImage[0] = $findViewById(R.id.iv_allow_anyone);
        verifyImage[1] = $findViewById(R.id.iv_need_verify);
        verifyImage[2] = $findViewById(R.id.iv_not_allow_add);
        verifyLayout[0] = $findViewById(R.id.ll_allow_anyone);
        verifyLayout[1] = $findViewById(R.id.ll_need_verify);
        verifyLayout[2] = $findViewById(R.id.ll_not_allow_add);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        if (null != bundle) {
            flag = bundle.getInt(KEY_FLAG, -1);
            role = bundle.getInt(KEY_ROLE, -1);
            id = bundle.getString(KEY_GID);
            level = bundle.getInt(KEY_SIZE, -1);
        }
        if (1 == flag) {
            tvTitle.setText(getResources().getString(R.string.group_name));
            tilName.setVisibility(View.VISIBLE);
        } else if (2 == flag) {
            tvTitle.setText(getResources().getString(role == 2 ? R.string.group_bulletin : R.string.group_intra));
            tieIntra.setHint(getResources().getString(role == 2 ? R.string.p_input_group_bulletin : R.string.p_input_group_intra));
            tilIntra.setVisibility(View.VISIBLE);
        } else if (3 == flag) {
            tvTitle.setText(getResources().getString(R.string.add_group_verify));
            llcVerify.setVisibility(View.VISIBLE);
        } else if(4 == flag){
            btnRight.setVisibility(View.GONE);
            tvTitle.setText(getResources().getString(R.string.group_size));
            llcSize.setVisibility(View.VISIBLE);
            if(0 == level){
                tvSize100.setVisibility(View.VISIBLE);
                tvSize200.setVisibility(View.VISIBLE);
                tvSize500.setVisibility(View.VISIBLE);
            } else if(1 == level){
                tvSize200.setVisibility(View.VISIBLE);
                tvSize500.setVisibility(View.VISIBLE);
                llSize100.setBackgroundResource(R.color.color_f6f6f6);
            } else if(2 == level){
                tvSize500.setVisibility(View.VISIBLE);
                llSize100.setBackgroundResource(R.color.color_f6f6f6);
                llSize200.setBackgroundResource(R.color.color_f6f6f6);
            } else if(3 == level || 4 == level || 5 == level){
                llSize100.setBackgroundResource(R.color.color_f6f6f6);
                llSize200.setBackgroundResource(R.color.color_f6f6f6);
                llSize500.setBackgroundResource(R.color.color_f6f6f6);
            }
        }
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

    @OnClick(R.id.btn_right)
    public void onClick() {
        if (1 == flag) {
            String groupName = etName.getText().toString();
            if (groupName.length() < 1) {
                etName.setError(getResources().getString(R.string.group_name_not_empty));
            } else if (groupName.length() > 64) {
                etName.setError(getResources().getString(R.string.group_name_limit_64));
            } else {
                presenter.modifyBasicGroupInfo(id, groupName, null);
            }
        } else if (2 == flag) {
            String groupIntra = tieIntra.getText().toString();
            if (groupIntra.length() < 1) {
                tieIntra.setError(getResources().getString(role == 2 ? R.string.group_bulletin_not_empty : R.string.group_intra_not_empty));
            } else if (groupIntra.length() > 600) {
                tieIntra.setError(getResources().getString(role == 2 ? R.string.group_bulletin_limit_600 : R.string.group_intra_limit_600));
            } else {
                presenter.modifyBasicGroupInfo(id, null, groupIntra);
            }
        } else if (3 == flag) {
            presenter.modifyGroupVerify(id, verify);
        }
    }

    @OnClick({R.id.ll_allow_anyone, R.id.ll_need_verify, R.id.ll_not_allow_add})
    public void onVerifyClick(View view) {
        for (int i = 0; i < 3; i++) {
            if (view == verifyLayout[i]) {
                verify = i;
                verifyImage[i].setVisibility(View.VISIBLE);
            } else {
                verifyImage[i].setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.tv_size_100, R.id.tv_size_200, R.id.tv_size_500})
    public void onSizeClick(View view) {
        switch (view.getId()) {
            case R.id.tv_size_100:
                presenter.modifyGroupSize(id, 1);
                break;
            case R.id.tv_size_200:
                presenter.modifyGroupSize(id, 2);
                break;
            case R.id.tv_size_500:
                presenter.modifyGroupSize(id, 3);
                break;
        }
    }

    @Override
    public void setGroupInfo(GroupInfoEntity entity) {
        if (entity == null || TextUtils.isEmpty(entity.getGid())) {
            if (1 == flag) {
                FunctionUtil.toastMessage("修改群名称失败");
            } else if (2 == flag) {
                FunctionUtil.toastMessage(role == 2 ? "修改群公告失败" : "修改群名称失败");
            }
        } else {
            Intent intent = new Intent();
            if (1 == flag) {
                intent.putExtra(KEY_KEY, entity.getName());
            } else if (2 == flag) {
                intent.putExtra(KEY_KEY, entity.getIntra());
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void setGroupSettings(GroupSettingEntity entity) {
        if (entity == null) {
            FunctionUtil.toastMessage("修改加群验证失败");
        } else {
            Intent intent = new Intent();
            intent.putExtra(KEY_KEY, entity.getAPPLY_ROLE());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void setGroupSizeResult(boolean result, int level) {
        if(result){
            Intent intent = new Intent();
            intent.putExtra(KEY_KEY, level);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            FunctionUtil.toastMessage("修改群规模失败");
        }
    }
}
