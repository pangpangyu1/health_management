package com.pangpangyu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pangpangyu.constant.MessageConstant;
import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.entity.Result;
import com.pangpangyu.pojo.Member;

import com.pangpangyu.service.MemberService;
import com.pangpangyu.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检查项管理
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Reference      //dubbo注解，到zookeeper服务注册中心查找MemberService接口服务
    private MemberService memberService;

    //检查项分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = memberService.pageQuery(queryPageBean);
        return pageResult;
    }


    @RequestMapping("/delete")
    public Result delete(Integer id) {
        try {
            memberService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    //修改
    @RequestMapping("/edit")
    public Result edit(@RequestBody Member member){
        try {
            System.out.println(member);
            memberService.edit(member);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    //根据Id查询
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Member member = memberService.findById(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,member);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    //查询全部
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<Member> list = memberService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }
}
