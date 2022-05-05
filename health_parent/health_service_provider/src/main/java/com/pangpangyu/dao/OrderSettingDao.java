package com.pangpangyu.dao;

import com.pangpangyu.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public void add(OrderSetting orderSetting);
    public void editNumberByOrderDate(OrderSetting orderSetting);
    public long findCountByOrderDate(Date orderDate);

    public List<OrderSetting> getOrderSettingByMonth(Map<String, String> map);

    public OrderSetting findByOrderDate(Date orderDate);
    public void editReservationsByOrderDate(OrderSetting orderSetting);


}
