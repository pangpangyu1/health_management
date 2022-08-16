package com.pangpangyu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pangpangyu.dao.MemberDao;
import com.pangpangyu.dao.OrderDao;
import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.pojo.Member;
import com.pangpangyu.service.MemberService;
import com.pangpangyu.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员服务
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    //保存会员信息
    public void add(Member member) {
        String password = member.getPassword();
        if(password != null){
            //使用md5将明文密码进行加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    //根据月份查询会员数量
    public List<Integer> findMemberCountByMonths(List<String> months) {
        List<Integer> memberCount = new ArrayList<>();
        String date = null;
        for (String month : months) {
            month = month + ".28";//格式：2019.04.31
            Integer count = memberDao.findMemberCountBeforeDate(month);
            memberCount.add(count);
        }
        return memberCount;
    }


    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //完成分页查询是基于mybatis框架提供的分页助手插件完成
        PageHelper.startPage(currentPage, pageSize);
        Page<Member> page = memberDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<Member> rows = page.getResult();
        return new PageResult(total, rows);
    }


    public void deleteById(Integer id) {
        orderDao.deleteByMemberId(id);
        memberDao.deleteById(id);
    }


    public void edit(Member checkItem) {
        System.out.println(checkItem);
        memberDao.edit(checkItem);
    }


    public Member findById(Integer id) {
        return memberDao.findById(id);
    }


    public List<Member> findAll() {
        return memberDao.findAll();
    }
}
