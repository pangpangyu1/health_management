package com.pangpangyu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pangpangyu.constant.MessageConstant;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.Setmeal;
import com.pangpangyu.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;
    //查询所有套餐
    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try {
            List<Setmeal> list = setmealService.findAll();
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
    //根据id查询套餐信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
}
