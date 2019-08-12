package com.itheima.service;

import com.itheima.pojo.User;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据用户名查询用户对象
     * @param name
     * @return
     */
    User findUserByUsername(String name);


}
