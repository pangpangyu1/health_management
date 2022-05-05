package com.pangpangyu.dao;

import com.pangpangyu.pojo.Role;

import java.util.Set;

public interface RoleDao {

    public Set<Role> findByUserId(Integer userId);
}
