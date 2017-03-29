package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;

/**
 * Created by 卫彪 on 2016/11/18.
 */
public interface GroupInfoView extends BaseView{

    /**
     * 设置群信息
     * @param entity
     */
    void setGroupInfo(GroupInfoEntity entity);

    /**
     * 设置群设置信息
     * @param entity
     */
    void setGroupSettings(GroupSettingEntity entity);

    void setGroupSizeResult(boolean result, int level);

}
