package com.ioyouyun.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.search.presenter.SearchPresenter;
import com.ioyouyun.search.view.SearchView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.GotoActivityUtils;

public class SearchActivity extends BaseActivity<SearchView, SearchPresenter> implements SearchView {

    private View searchResult;
    private Button searchBtn;
    private EditText etContent;
    private TextView tvTitle;
    private ImageView searchImage;
    private TextView searchNameText;
    private TextView searchTypeText;
    private Button clickBtn;

    private int searchType = 0; // 1: 搜好友 2：搜群
    private int type = 0;
    private GroupInfoEntity groupInfoEntity;
    private UserInfoEntity userInfoEntity;

    @Override
    protected SearchPresenter initPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        searchBtn = $findViewById(R.id.btn_search);
        etContent = $findViewById(R.id.et_content);
        tvTitle = $findViewById(R.id.tv_title);
        searchResult = $findViewById(R.id.rl_search_result);
        searchImage = $findViewById(R.id.iv_icon);
        searchNameText = $findViewById(R.id.tv_name);
        searchTypeText = $findViewById(R.id.tv_type);
        clickBtn = $findViewById(R.id.btn_confirm);
    }

    @Override
    protected void setListener() {
        searchBtn.setOnClickListener(this);
        clickBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundle = $getIntentExtra();
        searchType = bundle.getInt(KEY_FLAG, 0);
        if (1 == searchType) {
            tvTitle.setText(getResources().getString(R.string.add_friend));
            etContent.setHint(getResources().getString(R.string.please_input_uid));
        } else if (2 == searchType) {
            tvTitle.setText(getResources().getString(R.string.add_group));
            etContent.setHint(getResources().getString(R.string.please_input_gid));
        }

    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                searchResult.setVisibility(View.GONE);
                String id = etContent.getText().toString();
                if(TextUtils.isEmpty(id)){
                    return;
                }
                if (1 == searchType) {
                    presenter.searchUser(id);
                } else if (2 == searchType) {
                    presenter.searchGroup(id);
                }
                break;
            case R.id.btn_confirm:
                switch (type){
                    case 1:
                        // 进入聊天室
                        if(3 == groupInfoEntity.getRole() || 4 == groupInfoEntity.getRole()){
                            // 管理员或群组
                        } else {

                        }
                        gotoChat(groupInfoEntity.getGid(), groupInfoEntity.getName(), 2, groupInfoEntity.getRole());
                        break;
                    case 2:
                        // 群聊在群内
                        gotoChat(groupInfoEntity.getGid(), groupInfoEntity.getName(), 1, groupInfoEntity.getRole());
                        break;
                    case 3:
                        // 申请入群
                        presenter.applyAddGroup(FunctionUtil.nickname + "(" + FunctionUtil.uid + ")", groupInfoEntity.getGid());
                        break;
                    case 4:
                        // 单聊发消息
                        gotoChat(userInfoEntity.getId(), userInfoEntity.getNickname(), 0, 0);
                        break;
                    case 5:
                        // 加好友
                        presenter.addUser(userInfoEntity.getId());
                        break;
                }
                break;
        }
    }

    /**
     *
     * @param toId
     * @param name
     * @param type 聊天类型 0:单聊 1：群聊 2：聊天室
     */
    private void gotoChat(String toId, String name, int type, int role){
        GotoActivityUtils.INSTANSE.transferChatActivity(this, 0, toId, name, type, role, null);
        finish();
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
    public void searchResult(Object obj) {
        if (obj != null) {
            searchResult.setVisibility(View.VISIBLE);
            if (1 == searchType) {
                searchTypeText.setVisibility(View.GONE);
                userInfoEntity = (UserInfoEntity) obj;
                searchNameText.setText(userInfoEntity.getNickname());
                boolean isFriend = true; // 是否已经是好友
                if(isFriend){
                    clickBtn.setText("发消息");
                    type = 4;
                }else{
                    clickBtn.setText("加好友");
                    type = 5;
                }
            } else if (2 == searchType) {
                searchTypeText.setVisibility(View.VISIBLE);
                groupInfoEntity = (GroupInfoEntity) obj;
                searchNameText.setText(groupInfoEntity.getName());
                if(3 == groupInfoEntity.getCat1()){
                    // 聊天室
                    type = 1;
                    clickBtn.setText("进入聊天室");
                    searchTypeText.setText("聊天室（" + groupInfoEntity.getMembers() +  "）人");
                } else if(2 == groupInfoEntity.getCat1()){
                    // 公开群
                    searchTypeText.setText("群组（" + groupInfoEntity.getMembers() +  "）人");
                    if(groupInfoEntity.getRole() > 0){
                        // 在群内
                        type = 2;
                        clickBtn.setText("发消息");
                    }else{
                        // 不在群内
                        type = 3;
                        clickBtn.setText("申请加入");
                    }
                }

            }

        } else {
            if (1 == searchType) {
                FunctionUtil.toastMessage("没有找到此好友");
            } else if (2 == searchType) {
                FunctionUtil.toastMessage("没有找到此群");
            }
        }
    }

    @Override
    public void applyAddGroup(boolean result) {
        if(result){
            FunctionUtil.toastMessage("已发送请求");
        } else {
            FunctionUtil.toastMessage("请求失败，请重试");
        }
    }

}
