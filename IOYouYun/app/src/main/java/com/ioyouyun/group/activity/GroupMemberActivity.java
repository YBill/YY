package com.ioyouyun.group.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.contacts.InviteMemberActivity;
import com.ioyouyun.group.TitleDividerItemDecoration;
import com.ioyouyun.group.adapter.GroupMemberAdapter2;
import com.ioyouyun.group.model.ChatRoomInfoEntity;
import com.ioyouyun.group.model.GroupMemberEntity;
import com.ioyouyun.group.presenter.GroupMemberPresenter;
import com.ioyouyun.group.view.GroupMemberView;
import com.ioyouyun.home.widgets.DividerItemDecoration;
import com.ioyouyun.wchat.message.ConvType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMemberActivity extends BaseActivity<GroupMemberView, GroupMemberPresenter> implements GroupMemberView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_group_member)
    RecyclerView recyclerView;

    private String groupId;
    private ConvType convType;
    private int role;

    private GroupMemberAdapter2 groupMemberAdapter2;
    private TitleDividerItemDecoration titleDividerItemDecoration;
    private int allMemberNum = 0;

    @Override
    protected GroupMemberPresenter initPresenter() {
        return new GroupMemberPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getGroupMember(groupId, convType);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back(){
        Intent intent = new Intent();
        intent.putExtra(KEY_KEY, allMemberNum);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void $setToolBar() {
        Toolbar toolbar = $findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    back();
                }
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_member;
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
        if (null != bundle) {
            groupId = bundle.getString(KEY_GID);
            role = bundle.getInt(KEY_ROLE, 0);
            int type = bundle.getInt(KEY_TYPE, 0);
            if (type == 1)
                convType = ConvType.group;
            else if (type == 2) {
                convType = ConvType.room;
            }
        }

        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupMemberAdapter2 = new GroupMemberAdapter2(this, role);
        recyclerView.setAdapter(groupMemberAdapter2);
        recyclerView.addItemDecoration(titleDividerItemDecoration = new TitleDividerItemDecoration(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        groupMemberAdapter2.setOnItemClickLitener(new GroupMemberAdapter2.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                int clickRole = 0;
                String uid = "";
                boolean gag = false;
                Object entity = groupMemberAdapter2.getItem(position);
                if(entity instanceof GroupMemberEntity){
                    GroupMemberEntity groupMemberEntity = (GroupMemberEntity) entity;
                    clickRole = groupMemberEntity.getRole();
                    uid = groupMemberEntity.getId();
                    gag = groupMemberEntity.getGroupuser_props() != null ? groupMemberEntity.getGroupuser_props().isGag() : false;
                } else if(entity instanceof ChatRoomInfoEntity){
                    ChatRoomInfoEntity chatRoomInfoEntity = (ChatRoomInfoEntity) entity;
                    clickRole = chatRoomInfoEntity.getRole();
                    uid = chatRoomInfoEntity.getUid();
                    gag = chatRoomInfoEntity.getProps() != null ? chatRoomInfoEntity.getProps().isGag() : false;
                }
                if (entity != null) {
                    if ((role == 4 && (clickRole == 1 || clickRole == 3)) || (role == 3 && clickRole == 1))
                        showSwitchDialog(role, clickRole, uid, gag);
                }
            }
        });

    }

    /**
     * 选着对话框
     *
     * @param currentRole 当前人员身份
     * @param clickRole 被点击者身份
     * @param gag 用户是否被禁言
     */
    View view;
    Dialog dialog;
    private void showSwitchDialog(final int currentRole, final int clickRole, final String uid, final boolean gag) {
        if(view  == null){
            view = getLayoutInflater().inflate(R.layout.layout_operate_member_dialog, null);
            dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.onWindowAttributesChanged(wl);
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
        Button btnCancle = (Button) view.findViewById(R.id.btn_cancle);
        Button btn1 = (Button) view.findViewById(R.id.btn_1);
        Button btn4 = (Button) view.findViewById(R.id.btn_4);
        Button btn2 = (Button) view.findViewById(R.id.btn_2);
        Button btn3 = (Button) view.findViewById(R.id.btn_3);
        View line2 = view.findViewById(R.id.v_2);
        View line3 = view.findViewById(R.id.v_3);
        if (4 == currentRole) {
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            if(gag)
                btn2.setText(getResources().getString(R.string.relieve_banned));
            else
                btn2.setText(getResources().getString(R.string.banned));
            btn3.setText(getResources().getString(R.string.del_member));
            btn4.setText(getResources().getString(R.string.group_manager_transfer));
            if (3 == clickRole) {
                btn1.setText(getResources().getString(R.string.relieve_administrato));
            } else if (1 == clickRole) {
                btn1.setText(getResources().getString(R.string.upgrade_administrato));
            }
        } else if (3 == currentRole && 1 == clickRole) {
            if(gag)
                btn1.setText(getResources().getString(R.string.relieve_banned));
            else
                btn1.setText(getResources().getString(R.string.banned));
            btn4.setText(getResources().getString(R.string.del_member));
        }
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                if (4 == currentRole) {
                    if (1 == clickRole) {
                        // 升级管理员
                        presenter.upgradeAdministrato(groupId, uid, convType);
                    } else if (3 == clickRole) {
                        // 解除管理员
                        presenter.relieveAdministrato(groupId, uid, convType);
                    }
                } else if(3 == currentRole){
                    // 禁言
                    if(gag){
                        presenter.bannedSpeak(groupId, uid, 0, false, convType);
                    }else
                        showTimeDialog(uid);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                // 禁言
                if(gag){
                    presenter.bannedSpeak(groupId, uid, 0, false, convType);
                }else
                    showTimeDialog(uid);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                // 删除成员
                presenter.delMember(groupId, uid, convType);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                if (4 == currentRole) {
                    // 群主转让
                    presenter.groupManagerTransfer(groupId, uid, convType);
                } else if(3 == currentRole){
                    // 删除成员
                    presenter.delMember(groupId, uid, convType);
                }
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
    }

    private void showTimeDialog(final String uid){
        View view = View.inflate(this, R.layout.layout_gag_picker, null);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setVisibility(View.GONE);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(15);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.gag_time)).setView(view)
                .setPositiveButton(getResources().getString(R.string.string_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long time = timePicker.getCurrentHour() * 60 + timePicker.getCurrentMinute();
                        if(time > 0)
                            presenter.bannedSpeak(groupId, uid, time, true, convType);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_cancle), null).create();
        alertDialog.show();
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (ConvType.group == convType && (role == 4 || role == 3))
            getMenuInflater().inflate(R.menu.group_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group_member) {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_FLAG, 1);
            bundle.putString(KEY_GID, groupId);
            $startActivity(InviteMemberActivity.class, bundle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshAdapter(List<?> list) {
        if (list != null) {
            allMemberNum = list.size();
            tvTitle.setText(getResources().getString(R.string.group_member) + "(" + allMemberNum + ")");
            titleDividerItemDecoration.setGroupMemberEntityList(list);
            groupMemberAdapter2.setGroupMemberList(list);
            groupMemberAdapter2.notifyDataSetChanged();
        }
    }

    @Override
    public void operateMemberResult(boolean result) {
        if (result) {
            presenter.getGroupMember(groupId, convType);
        }
    }

    @Override
    public void setListView(List<?> list) {
        refreshAdapter(list);
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
