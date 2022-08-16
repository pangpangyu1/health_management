package com.pangpangyu.dao;

import com.github.pagehelper.Page;
import com.pangpangyu.pojo.Member;
import com.pangpangyu.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    public Map findById4Detail(Integer id);
    public Integer findOrderCountByDate(String date);
    public Integer findOrderCountAfterDate(String date);
    public Integer findVisitsCountByDate(String date);
    public Integer findVisitsCountAfterDate(String date);
    public List<Map> findHotSetmeal();
    public void add(Order order);
    public List<Order> findByCondition(Order order);

    void deleteByMemberId(Integer id);

    Page<Order> selectByCondition(String queryString);

    void deleteById(Integer id);

    void edit(Order checkItem);
}

