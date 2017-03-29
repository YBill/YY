package com.ioyouyun.search.view;


import com.ioyouyun.base.BaseView;

/**
 * Created by 卫彪 on 2016/10/24.
 */
public interface SearchView extends BaseView {

    /**
     * 搜索结果
     * @param entity
     */
    void searchResult(Object entity);

    /**
     * 加群结果
     * @param result
     */
    void applyAddGroup(boolean result);

}
