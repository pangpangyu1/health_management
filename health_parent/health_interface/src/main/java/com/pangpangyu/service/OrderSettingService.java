package com.pangpangyu.service;

import com.pangpangyu.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    public void add(List<OrderSetting> list);

    public List<Map> getOrderSettingByMonth(String date);

    public void editNumberByDate(OrderSetting orderSetting);

}
