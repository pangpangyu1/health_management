package com.pangpangyu.service;

import com.pangpangyu.pojo.Member;
import com.pangpangyu.pojo.User;

public interface UserService {
    public User findByUsername(String name);

    void edit(User member);
}
