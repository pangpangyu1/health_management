package com.pangpangyu.service;

import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {
    /**
     * 添加
     * @param checkItem
     */
    public void add(CheckItem checkItem);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    public PageResult pageQuery(QueryPageBean queryPageBean);

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Integer id);

    /**
     * 编辑
     * @param checkItem
     */
    public void edit(CheckItem checkItem);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
