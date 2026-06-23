package com.library.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.system.entity.User;

/**
 * 用户管理服务接口
 */
public interface UserService {

    /** 分页查询用户列表 */
    Page<User> pageUsers(int current, int size, String keyword);

    /** 根据ID查询用户 */
    User getUserById(Long id);

    /** 新增用户 */
    void addUser(User user);

    /** 更新用户信息 */
    void updateUser(User user);

    /** 删除用户（逻辑删除） */
    void deleteUser(Long id);

    /** 重置密码 */
    void resetPassword(Long id, String newPassword);

    /** 更新用户状态（启用/禁用） */
    void updateStatus(Long id, Integer status);
}
