package com.ioyouyun.group.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.CreateGroupPresenter;
import com.ioyouyun.group.view.CreateGroupView;
import com.ioyouyun.utils.GotoActivityUtils;

public class CreateGroupActivity extends BaseActivity<CreateGroupView, CreateGroupPresenter> implements CreateGroupView {

    private Button createBtn;
    private EditText groupNameEdit;
    private EditText groupIntraEdit;
    private TextView gNameLimitText;
    private TextView gIntraLimitText;
    private TextView titleText;
    private TextView nameText;
    private TextView intraText;

    private int createType = 0;

    @Override
    protected CreateGroupPresenter initPresenter() {
        return new CreateGroupPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_group;
    }

    @Override
    protected void initView() {
        createBtn = $findViewById(R.id.tv_confirm_create);
        groupNameEdit = $findViewById(R.id.et_group_name);
        groupIntraEdit = $findViewById(R.id.et_group_intra);
        gNameLimitText = $findViewById(R.id.tv_gname_limit);
        gIntraLimitText = $findViewById(R.id.tv_gintra_limit);
        titleText = $findViewById(R.id.tv_title);
        nameText = $findViewById(R.id.tv_name);
        intraText = $findViewById(R.id.tv_intra);
    }

    @Override
    protected void setListener() {
        createBtn.setOnClickListener(this);
        groupNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                gNameLimitText.setText(groupNameEdit.getText().length() + "/64");
            }
        });
        groupIntraEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                gIntraLimitText.setText(groupIntraEdit.getText().length() + "/600");
            }
        });
    }

    @Override
    protected void initData() {
        loddingDialog.setMessage("创建中...");
        Bundle bundle = $getIntentExtra();
        createType = bundle.getInt(KEY_FLAG, 0);
        if (1 == createType) {
            createBtn.setText(getResources().getString(R.string.next));
            titleText.setText(getResources().getString(R.string.create_group));
            nameText.setText(getResources().getString(R.string.group_name));
            intraText.setText(getResources().getString(R.string.group_intra));
        }else if (2 == createType) {
            createBtn.setText(getResources().getString(R.string.create));
            titleText.setText(getResources().getString(R.string.create_chat_room));
            nameText.setText(getResources().getString(R.string.chat_room_name));
            intraText.setText(getResources().getString(R.string.chat_room_intra));
        }
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_create:
                String name = groupNameEdit.getText().toString().trim();
                String intra = groupIntraEdit.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    if (1 == createType) {
                        groupNameEdit.setError("请输入群名称");
                    }else if (2 == createType) {
                        groupNameEdit.setError("请输入聊天室名称");
                    }
                    return;
                }
                GroupInfoEntity entity = new GroupInfoEntity();
                entity.setName(name);
                entity.setIntra(intra);
                if(1 == createType) {
                    gotoGroupSize(entity);
                } else if (2 == createType) {
                    presenter.createChatRoom(entity);
                }
                break;
        }
    }

    private void gotoGroupSize(GroupInfoEntity entity){
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_KEY, entity);
        $startActivity(GroupSizeActivity.class, bundle);
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
        // 到聊天室
        GotoActivityUtils.INSTANSE.transferChatActivity(this, 1, entity.getGid(), entity.getName(), 2, entity.getRole(), null);
        this.finish();
    }

}
