package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MenuDao;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户服务实现类
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private MenuDao menuDao;

    /**
     * 查询用户对象（角色+权限数据）
     *
     * @param username
     * @return
     */
    @Override
    public User findUserByUsername(String username) {
        //先根据用户名查询用户对象
        User user = userDao.findUserByUsername(username);
        //根据用户id查询角色对象
        if (user == null) {
            return null;
        }
        Integer userId = user.getId();//用户id
        Set<Role> roleSet = roleDao.findRoleByUserId(userId);
        //再根据角色id查询权限对象
        if (roleSet != null && roleSet.size() > 0) {
            for (Role role : roleSet) {
                Integer roleId = role.getId();///角色id
                Set<Permission> permissionSet = permissionDao.findPermissionByRoleId(roleId);
                if (permissionSet != null && permissionSet.size() > 0) {
                    //将权限放入角色对象中
                    role.setPermissions(permissionSet);
                }
                LinkedHashSet<Menu> menus = menuDao.findMenusByRoleId(roleId);
                if (menus != null && menus.size() > 0) {
                    for (Menu menu : menus) {
                        List<Menu> menuList = menuDao.findMenusByMenuId(roleId,menu.getId());
                        menu.setChildren(menuList);
                    }
                    role.setMenus(menus);
                }
            }
            //将角色数据放入用户对象中
            user.setRoles(roleSet);
        }
        return user;
    }
}
