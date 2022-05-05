package com.pangpangyu.dao;

import com.pangpangyu.pojo.Permission;

import java.util.Set;

public interface PermissionDao {

    public Set<Permission> findByRoleId(Integer roleId);
}
