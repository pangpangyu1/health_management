package com.pangpangyu.service;

import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    /**
     * 新增套餐
     * @param setmeal
     * @param checkgroupIds
     */
    public void add(Setmeal setmeal, Integer[] checkgroupIds);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public void edit(Setmeal setmeal, Integer[] checkgroupIds);

    public Setmeal findById(Integer id);

    public List<Integer> findCheckGroupIdsBySetmealId(Integer id);

    public void deleteById(Integer id);

    public List<Setmeal> findAll();

    public List<Map<String, Object>> findSetmealCount();
}
