package com.ioyouyun.group.view;

import com.ioyouyun.base.BaseView;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.model.GroupSettingEntity;

/**
 * Created by 卫彪 on 2016/11/7.
 */
public interface GroupSetView extends BaseView{

    /**
     * 设置群信息
     * @param entity
     * @param settingEntity
     */
    void setGroupInfo(GroupInfoEntity entity, GroupSettingEntity settingEntity);

    /**
     * 退群
     * @param gid
     * @param result
     */
    void exitGroup(String gid, boolean result);

    /**
     * 解散群
     * @param gid
     * @param result
     */
    void delGroup(String gid, boolean result);

}
