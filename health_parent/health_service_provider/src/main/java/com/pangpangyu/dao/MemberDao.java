package com.pangpangyu.dao;

import com.github.pagehelper.Page;
import com.pangpangyu.pojo.Member;
import com.pangpangyu.pojo.Order;

import java.util.List;

public interface MemberDao {

    public List<Member> findAll();
    public Page<Member> selectByCondition(String queryString);
    public void add(Member member);
    public void deleteById(Integer id);
    public Member findById(Integer id);
    public List<Order> findBySetmealId(Integer id);
    public Member findByTelephone(String telephone);
    public void edit(Member member);
    public Integer findMemberCountBeforeDate(String date);
    public Integer findMemberCountByDate(String date);
    public Integer findMemberCountAfterDate(String date);
    public Integer findMemberTotalCount();
}
