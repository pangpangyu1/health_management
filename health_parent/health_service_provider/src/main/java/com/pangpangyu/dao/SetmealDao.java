package com.pangpangyu.dao;

import com.github.pagehelper.Page;
import com.pangpangyu.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);

    public void setCheckGroupAndCheckItem(Map<String, Integer> map);

    public Page<Setmeal> findByCondition(String queryString);

    public List<Integer> findCheckGroupIdsBySetmealId(Integer id);

    public Setmeal findById(Integer id);

    public void deleteById(Integer id);

    public void deleteAssociation(Integer id);

    public void edit(Setmeal setmeal);

    public List<Setmeal> findAll();

    public List<Map<String, Object>> findSetmealCount();

}
