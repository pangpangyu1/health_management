package com.pangpangyu.dao;

import com.pangpangyu.pojo.User;

public interface UserDao {
    public User findByUsername(String username);

    void updatePassword(User user);
}
