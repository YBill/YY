package com.ioyouyun.group.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.adapter.GagMemberAdapter;
import com.ioyouyun.group.presenter.GagMemberPresenter;
import com.ioyouyun.group.view.GagMemberView;
import com.ioyouyun.home.widgets.DividerItemDecoration;
import com.ioyouyun.wchat.message.ConvType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 禁言成员
 */
public class GagMemberActivity extends BaseActivity<GagMemberView, GagMemberPresenter> implements GagMemberView {

    @BindView(R.id.sc_all_gag)
    SwitchCompat scAllGag;
    @BindView(R.id.rv_gag_member)
    RecyclerView rvGagMember;

    private GagMemberAdapter gagMemberAdapter;

    private String groupId;
    private ConvType convType;
    private int role;

    @Override
    protected GagMemberPresenter initPresenter() {
        return new GagMemberPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gag_member;
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

        rvGagMember.setMotionEventSplittingEnabled(false);
        rvGagMember.setLayoutManager(new LinearLayoutManager(this));
        gagMemberAdapter = new GagMemberAdapter(this);
        rvGagMember.setAdapter(gagMemberAdapter);
        rvGagMember.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvGagMember.setItemAnimator(new DefaultItemAnimator());
        gagMemberAdapter.setOnDelClickLitener(new GagMemberAdapter.OnDelClickLitener() {
            @Override
            public void onClick(View view, String uid) {
                presenter.relieveBanned(groupId, uid, convType);
            }
        });

        presenter.getGagMember(groupId, convType);
    }

    @Override
    public void widgetClick(View v) {

    }

    private void refreshAdapter(List<String> list) {
        if (list != null) {
            gagMemberAdapter.setGagMemberList(list);
            gagMemberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void operateMemberResult(boolean result) {
        if (result) {
            presenter.getGagMember(groupId, convType);
        }
    }

    @Override
    public void setListView(List<String> list) {
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
