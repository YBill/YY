package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;

/**
 * Created by 卫彪 on 2016/10/20.
 */
public interface AddGroupVerifyView extends BaseView {

    /**
     * 创建群成功
     *
     * @param entity
     */
    void createSuccess(GroupInfoEntity entity);
}
