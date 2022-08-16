package com.pangpangyu.service;

import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.Member;
import com.pangpangyu.pojo.Order;

import java.util.Map;

public interface OrderService {
    public Result order(Map map) throws Exception;
    public Map findById(Integer id) throws Exception;

    PageResult pageQuery(QueryPageBean queryPageBean);

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Integer id);


}
