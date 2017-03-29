package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;

import java.util.List;


/**
 * Created by 卫彪 on 2016/11/24.
 */
public interface GagMemberView extends BaseView {

    /**
     * 设置数据
     *
     * @param list
     */
    void setListView(List<String> list);

    /**
     * 操作用户
     *
     * @param result
     */
    void operateMemberResult(boolean result);

}
