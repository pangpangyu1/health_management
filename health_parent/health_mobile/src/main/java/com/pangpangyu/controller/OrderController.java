package com.pangpangyu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pangpangyu.constant.MessageConstant;
import com.pangpangyu.constant.RedisMessageConstant;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.Order;
import com.pangpangyu.service.OrderService;
import com.pangpangyu.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
//        String telephone = (String) map.get("telephone");
        //从Redis中获取缓存的验证码，key为手机号+RedisConstant.SENDTYPE_ORDER
//        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
//        String validateCode = (String) map.get("validateCode");
        //校验手机验证码
//        if(codeInRedis == null || !codeInRedis.equals(validateCode)){
//            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
//        }
        Result result =null;
        //调用体检预约服务
        try{
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            result = orderService.order(map);
        }catch (Exception e){
            e.printStackTrace();
            //预约失败
            return result;
        }
//        if(result.isFlag()){
//            //预约成功，发送短信通知
//            String orderDate = (String) map.get("orderDate");
//            try {
//                SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,orderDate);
//            } catch (ClientException e) {
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    //根据预约ID查询预约相关信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
