package com.pangpangyu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pangpangyu.dao.CheckGroupDao;
import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.pojo.CheckGroup;
import com.pangpangyu.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//如果加了事务注解，必须明确当前服务实现的是哪个服务接口
//在Dubbo的@Service注解类中，要想实现事务管理，必须在@Service后面加上(interfaceClass = “实现的接口.class”)
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //新增检查组，操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        //设置检查组和检查项的多对多关联关系，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId();
        this.setCheckGroupAndCheckItem(checkGroupId,checkitemIds);

    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //清理当前检查组关联的检查项，操作中间关系表
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //重新建立关联关系
        Integer checkGroupId = checkGroup.getId();
//        System.out.println(checkGroup.getId());
        this.setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
        //修改检查表基本信息，操作检查组表
        checkGroupDao.edit(checkGroup);
    }

    @Override
    public void deleteById(Integer id) {
        //先清楚关联关系
        checkGroupDao.deleteAssociation(id);
        //再删除检查组
        checkGroupDao.deleteById(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    //向中间表(t_checkgroup_checkitem)插入数据（建立检查组和检查项关联关系）
    public void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds) {
//        System.out.println(checkGroupId);
        if (checkitemIds != null && checkitemIds.length > 0) {
            for (Integer checkitemId : checkitemIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("checkgroup_id", checkGroupId);
                map.put("checkitem_id", checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

}
