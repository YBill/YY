package com.ioyouyun.group.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.GroupSizePresenter;
import com.ioyouyun.group.view.GroupSizeView;

/**
 * 群规模Activity
 */
public class GroupSizeActivity extends BaseActivity<GroupSizeView, GroupSizePresenter> implements GroupSizeView {

    LinearLayout[] groupSizeLayout = new LinearLayout[4];
    ImageView[] groupSizeImage = new ImageView[4];
    private Button nextBtn;
    private GroupInfoEntity entity;
    private int level = 0;

    @Override
    protected GroupSizePresenter initPresenter() {
        return new GroupSizePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_size;
    }

    @Override
    protected void initView() {
        nextBtn = $findViewById(R.id.btn_next);
        groupSizeLayout[0] = $findViewById(R.id.ll_50);
        groupSizeLayout[1] = $findViewById(R.id.ll_100);
        groupSizeLayout[2] = $findViewById(R.id.ll_200);
        groupSizeLayout[3] = $findViewById(R.id.ll_500);
        groupSizeImage[0] = $findViewById(R.id.iv_50);
        groupSizeImage[1] = $findViewById(R.id.iv_100);
        groupSizeImage[2] = $findViewById(R.id.iv_200);
        groupSizeImage[3] = $findViewById(R.id.iv_500);
    }

    @Override
    protected void setListener() {
        nextBtn.setOnClickListener(this);
        for (int i = 0; i < 4; i++){
            groupSizeLayout[i].setOnClickListener(this);
        }
    }

    @Override
    protected void initData() {
        Bundle bundle = $getIntentExtra();
        entity = bundle.getParcelable(KEY_KEY);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void widgetClick(View v) {
        if(v == nextBtn) {
            entity.setLevel(level);
            gotoGroupVerify(entity);
        } else {
            for (int i = 0; i < 4; i++){
                if(v == groupSizeLayout[i]){
                    level = i;
                    groupSizeImage[i].setVisibility(View.VISIBLE);
                }else {
                    groupSizeImage[i].setVisibility(View.GONE);
                }
            }

        }
    }

    private void gotoGroupVerify(GroupInfoEntity entity){
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_KEY, entity);
        $startActivity(AddGroupVerifyActivity.class, bundle);
    }

}
