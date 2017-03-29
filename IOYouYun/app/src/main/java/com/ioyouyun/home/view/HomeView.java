package com.ioyouyun.home.view;

import com.ioyouyun.base.BaseView;

/**
 * Created by 卫彪 on 2016/8/12.
 */
public interface HomeView extends BaseView {

    /**
     * 设置Push提醒时段
     *
     * @param startTime
     * @param endTime
     */
    void setPushTime(int startTime, int endTime);

    /**
     * 切换版本
     */
    void switchVersion();

}
