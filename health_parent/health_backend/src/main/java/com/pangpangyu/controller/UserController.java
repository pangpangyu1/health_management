package com.pangpangyu.controller;

import com.pangpangyu.constant.MessageConstant;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    //获取当前登录用户的用户名
    @RequestMapping("/getUsername")
    public Result getUsername()throws Exception{
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println(user);
                return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
        }catch (Exception e){
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
    }
}
