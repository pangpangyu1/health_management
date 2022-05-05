package com.pangpangyu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pangpangyu.dao.OrderSettingDao;
import com.pangpangyu.pojo.OrderSetting;
import com.pangpangyu.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> list) {
        if (list != null && list.size() > 0) {
            for (OrderSetting orderSetting : list) {
                //判断当前日期是否进行了预约
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (countByOrderDate > 0) {
                    //已经进行了预约设置，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else {
                    //没有进行预约设置，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    public String[] setMonthFormat(String date){
        String begin = null;
        String end = null;
        String[] split = date.split("-");//4
        String monthNumber = split[1];
        String yearNumber = split[0];
        if (monthNumber != null) {
            if( "1".equals(monthNumber) || "3".equals(monthNumber) ||
                    "5".equals(monthNumber) || "7".equals(monthNumber) ||
                    "8".equals(monthNumber) || "10".equals(monthNumber) || "12".equals(monthNumber)) {
                begin = date + "-1";
                end = date + "-31";
            }else if("2".equals(monthNumber)){
                if(Integer.parseInt(yearNumber) % 4 == 0){
                    begin = date + "-1";
                    end = date + "-29";
                }else {
                    begin = date + "-1";
                    end = date + "-28";
                }
            }else if("4".equals(monthNumber) || "6".equals(monthNumber) ||
                    "9".equals(monthNumber) || "11".equals(monthNumber)){
                begin = date + "-1";
                end = date + "-30";
            }
        }
        String[] str = {begin, end};
        return str;
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {//格式：yyyy-MM
        String[] str = setMonthFormat(date);
        String begin = str[0];
        String end = str[1];
//        System.out.println(begin);
        Map<String,String> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (OrderSetting orderSetting : list) {
                Map<String,Object> m = new HashMap<>();
//               System.out.println(orderSetting.getOrderDate());
//               System.out.println(orderSetting.getOrderDate().getDate());
                m.put("date", orderSetting.getOrderDate().getDate());//获取日期数字（几号）
                m.put("number", orderSetting.getNumber());
                m.put("reservations", orderSetting.getReservations());
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //先根据日期查询是否已经进行了预约设置
        Date orderDate = orderSetting.getOrderDate();
        long count = orderSettingDao.findCountByOrderDate(orderDate);
        if (count > 0) {
            //已经进行了预约设置，执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //当前日期没有预约设置，需要执行插入操作
            orderSettingDao.add(orderSetting);
        }
    }
}
