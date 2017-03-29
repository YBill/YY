package com.ioyouyun.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;
import com.ioyouyun.group.presenter.GroupSetPresenter;
import com.ioyouyun.group.view.GroupSetView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.message.ConvType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群组和聊天室 设置
 * <p>
 * create on 2016/11/7 by Bill
 */
public class GroupSetActivity extends BaseActivity<GroupSetView, GroupSetPresenter> implements GroupSetView {

    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.tv_group_intra_title)
    TextView tvGroupIntraTitle;
    @BindView(R.id.tv_group_intra)
    TextView tvGroupIntra;
    @BindView(R.id.iv_group_intra)
    ImageView ivGroupIntra;
    @BindView(R.id.tv_group_type)
    TextView tvGroupType;
    @BindView(R.id.tv_group_id)
    TextView tvGroupId;
    @BindView(R.id.tv_group_size)
    TextView tvGroupSize;
    @BindView(R.id.tv_all_group_users)
    TextView tvAllGroupUsers;
    @BindView(R.id.tv_row_3_left)
    TextView tvRow3Left;
    @BindView(R.id.tv_row_3_right)
    TextView tvRow3Right;
    @BindView(R.id.tv_row_4_left)
    TextView tvRow4Left;
    @BindView(R.id.tv_row_4_right)
    TextView tvRow4Right;
    @BindView(R.id.sc_disturb)
    SwitchCompat scDisturb;
    @BindView(R.id.btn_red)
    Button btnRed;
    @BindView(R.id.ll_row_4)
    LinearLayout llRow4;
    @BindView(R.id.ll_row_3)
    LinearLayout llRow3;

    private static final int REQUEST_REFRESH_GROUP_NAME = 1000;
    private static final int REQUEST_REFRESH_GROUP_INTRA = 1001;
    private static final int REQUEST_REFRESH_GROUP_VERIFY = 1002;
    private static final int REQUEST_REFRESH_GROUP_SIZE = 1003;
    private static final int REQUEST_REFRESH_GROUP_ALL = 1004;

    private String id;
    private int role; // 群权限
    private int level;
    private ConvType convType;

    @Override
    protected GroupSetPresenter initPresenter() {
        return new GroupSetPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_set;
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
        if (null != bundle)
            id = bundle.getString(KEY_GID);

        presenter.getGroupInfo(id);
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

    private void setGroupSize(int level){
        String groupSize = "";
        if(0 == level)
            groupSize = "50人";
        else if(1 == level)
            groupSize = "100人";
        else if(2 == level)
            groupSize = "200人";
        else if(3 == level)
            groupSize = "500人";
        else if(4 == level)
            groupSize = "1000人";
        else if(5 == level)
            groupSize = "2000人";
        tvGroupSize.setText(groupSize);
    }

    private void setAllMember(int memberNum){
        String format = getResources().getString(R.string.all_group_users);
        String member = String.format(format, memberNum);
        tvAllGroupUsers.setText(member);
    }

    private void setGroupVerify(String verify){
        if("no".equals(verify))
            tvRow3Right.setText("允许任何人加入");
        else if("yes".equals(verify))
            tvRow3Right.setText("需身份验证");
        else if("not_allowed".equals(verify))
            tvRow3Right.setText("不允许加入");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            if(REQUEST_REFRESH_GROUP_NAME == requestCode){
                String groupName = data.getStringExtra(KEY_KEY);
                if(!TextUtils.isEmpty(groupName))
                    tvGroupName.setText(groupName);
            } else if(REQUEST_REFRESH_GROUP_INTRA == requestCode){
                String groupIntra = data.getStringExtra(KEY_KEY);
                if(!TextUtils.isEmpty(groupIntra))
                    tvGroupIntra.setText(groupIntra);
            } else if(REQUEST_REFRESH_GROUP_VERIFY == requestCode){
                String applyRole = data.getStringExtra(KEY_KEY);
                setGroupVerify(applyRole);
            } else if(REQUEST_REFRESH_GROUP_SIZE == requestCode){
                int groupSize = data.getIntExtra(KEY_KEY, -1);
                setGroupSize(groupSize);
            } else if(REQUEST_REFRESH_GROUP_ALL == requestCode){
                int memberNum = data.getIntExtra(KEY_KEY, -1);
                setAllMember(memberNum);
            }

        }
    }

    @OnClick({R.id.ll_group_name, R.id.ll_group_intra, R.id.ll_group_size, R.id.ll_all_group_users, R.id.ll_row_3, R.id.ll_row_4, R.id.tv_clear_chat, R.id.btn_red})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_group_name:
                modifyGroupName(1, convType, REQUEST_REFRESH_GROUP_NAME);
                break;
            case R.id.ll_group_intra:
                modifyGroupName(2, convType, REQUEST_REFRESH_GROUP_INTRA);
                break;
            case R.id.ll_group_size:
                modifyGroupSize();
                break;
            case R.id.ll_all_group_users:
                allUsers();
                break;
            case R.id.ll_row_3:
                rol3Click();
                break;
            case R.id.ll_row_4:
                rol4Click();
                break;
            case R.id.tv_clear_chat:
                presenter.clearLocalData(id);
                break;
            case R.id.btn_red:
                exitGroup();
                break;
        }
    }

    /**
     * 成员禁言或团队管理
     */
    private void rol4Click(){
        if(ConvType.group == convType && (4 == role || 3 == role)){
            // 成员禁言
            memberGag();
        } else if(ConvType.room == convType && 4 == role){
            // 团队管理

        }
    }

    /**
     * 加群验证或成员禁言
     */
    private void rol3Click(){
        if(ConvType.group == convType && 4 == role){
            // 加群验证
            modifyGroupName(3, convType, REQUEST_REFRESH_GROUP_VERIFY);
        } else if(ConvType.room == convType && (4 == role || 3 == role)){
            // 成员禁言
            memberGag();
        }
    }

    /**
     * 修改群规模
     */
    private void modifyGroupSize() {
        if (ConvType.group == convType && 4 == role) {
            modifyGroupName(4, convType, REQUEST_REFRESH_GROUP_SIZE);
        }
    }

    /**
     * 修改群名称和群简介
     * @param flag 1:群名称 2：群简介 3:群验证方式
     * @param requestCode 回调code
     */
    private void modifyGroupName(int flag, ConvType convType, int requestCode) {
        if (4 == role) {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_FLAG, flag);
            bundle.putString(KEY_GID, id);
            bundle.putInt(KEY_ROLE, ConvType.room == convType ? 2 : 1);
            bundle.putInt(KEY_SIZE, level);
            $startActivityForResult(GroupInfoActivity.class, bundle, requestCode);
        }
    }

    /**
     * 全部成员
     */
    private void allUsers(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_GID, id);
        bundle.putInt(KEY_ROLE, role);
        bundle.putInt(KEY_TYPE, ConvType.room == convType ? 2 : 1);
        $startActivityForResult(GroupMemberActivity.class, bundle, REQUEST_REFRESH_GROUP_ALL);
    }

    /**
     * 禁言成员
     */
    private void memberGag() {
        if (4 == role) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_GID, id);
            bundle.putInt(KEY_ROLE, role);
            bundle.putInt(KEY_TYPE, ConvType.room == convType ? 2 : 1);
            $startActivity(GagMemberActivity.class, bundle);
        }
    }

    /**
     * 退群
     */
    private void exitGroup(){
        String message;
        String ok;
        if (role == 4) {
            message = "确认解散群?";
            ok = "解散";
        } else {
            message = "确认退出群?";
            ok = "退出";
        }
        Snackbar.make(btnRed, message, Snackbar.LENGTH_LONG).setAction(ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role == 4) {
                    presenter.deleteGroup(id);
                } else {
                    presenter.exitGroup(id, convType);
                }
            }
        }).show();
    }

    @Override
    public void exitGroup(String gid, boolean result) {
        if (result) {
            if(convType == ConvType.room)
                FunctionUtil.toastMessage("退出聊天室成功");
            else
                FunctionUtil.toastMessage("退群成功");
            setResult(RESULT_OK);
            finish();
        } else{
            if(convType == ConvType.room)
                FunctionUtil.toastMessage("退出聊天室失败");
            else
                FunctionUtil.toastMessage("退群失败");
        }
    }

    @Override
    public void delGroup(String gid, boolean result) {
        if (result) {
            FunctionUtil.toastMessage("解散群成功");
            setResult(RESULT_OK);
            finish();
        } else
            FunctionUtil.toastMessage("解散群失败");
    }

    @Override
    public void setGroupInfo(GroupInfoEntity entity, GroupSettingEntity settingEntity) {
        role = entity.getRole();
        level = entity.getLevel();
        if (2 == entity.getCat1()) {
            convType = ConvType.group;
        } else if (3 == entity.getCat1()) {
            convType = ConvType.room;
        }

        tvGroupName.setText(entity.getName());
        tvGroupIntra.setText(entity.getIntra());
        tvGroupId.setText(entity.getGid());
        setGroupSize(level);
        setAllMember(entity.getMembers());

        if (ConvType.group == convType) {
            if(FunctionUtil.isOnlinePlatform) llRow3.setVisibility(View.GONE);
            tvGroupIntraTitle.setText("群简介");
            tvGroupType.setText("群组");
            tvRow3Left.setText("加群验证");
            if(settingEntity != null){
                setGroupVerify(settingEntity.getAPPLY_ROLE());
            }
            if (4 == role) {
                tvRow4Left.setText("成员禁言");
                llRow4.setVisibility(View.VISIBLE);
                btnRed.setText("解散该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
                ivGroupIntra.setVisibility(View.VISIBLE);
                tvGroupSize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
            } else if(3 == role){
                tvRow4Left.setText("成员禁言");
                llRow4.setVisibility(View.VISIBLE);
                btnRed.setText("退出该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ivGroupIntra.setVisibility(View.GONE);
                tvGroupSize.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else if (1 == role) {
                llRow4.setVisibility(View.GONE);
                btnRed.setText("退出该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ivGroupIntra.setVisibility(View.GONE);
                tvGroupSize.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        } else if (ConvType.room == convType) {
            tvGroupIntraTitle.setText("群公告");
            tvGroupType.setText("聊天室");
            if (4 == role) {
                tvRow3Left.setText("成员禁言");
                tvRow4Left.setText("管理团队");
                llRow4.setVisibility(View.VISIBLE);
                btnRed.setText("解散该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
                ivGroupIntra.setVisibility(View.VISIBLE);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
            } else if (3 == role) {
                tvRow3Left.setText("成员禁言");
                llRow4.setVisibility(View.GONE);
                btnRed.setText("退出该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ivGroupIntra.setVisibility(View.GONE);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more, 0);
            } else if (1 == role) {
                if(settingEntity != null){
                    setGroupVerify(settingEntity.getAPPLY_ROLE());
                }
                tvRow3Left.setText("加群验证");
                llRow4.setVisibility(View.GONE);
                btnRed.setText("退出该群");
                tvGroupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ivGroupIntra.setVisibility(View.GONE);
                tvRow3Right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            tvGroupSize.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        }


    }

}
