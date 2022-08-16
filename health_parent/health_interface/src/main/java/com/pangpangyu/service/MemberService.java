package com.pangpangyu.service;

import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.pojo.Member;
import com.pangpangyu.pojo.Member;

import java.util.List;

public interface MemberService {
    //根据手机号查询会员
    public Member findByTelephone(String telephone);
    public void add(Member member);
    public List<Integer> findMemberCountByMonths(List<String> months);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    public PageResult pageQuery(QueryPageBean queryPageBean);

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Integer id);

    /**
     * 编辑
     * @param checkItem
     */
    public void edit(Member checkItem);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Member findById(Integer id);

    public List<Member> findAll();

}
