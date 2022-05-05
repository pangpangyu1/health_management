package com.pangpangyu.service;

import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    /**
     * 检查组新增，同事让检查组关联检查项
     * @param checkGroup
     * @param checkitemIds
     */
    public void add(CheckGroup checkGroup, Integer[] checkitemIds);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    public PageResult pageQuery(QueryPageBean queryPageBean);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public CheckGroup findById(Integer id);

    /**
     * 根据检查组id查询检查组包含的多个检查项id
     * @param id
     * @return
     */
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    /**
     * 编辑
     * @param checkGroup
     * @param checkitemIds
     */
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Integer id);

    /**
     * 查询全部
     * @return
     */
    public List<CheckGroup> findAll();
}
