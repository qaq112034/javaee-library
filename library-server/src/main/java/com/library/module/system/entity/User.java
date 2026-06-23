package com.library.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体
 * <p>
 * 用户登录系统的账号，与读者（Reader）是不同的概念：
 * - User:   系统操作账号（管理员、图书管理员等）
 * - Reader: 图书馆的读者/借阅者
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /** 用户名（登录账号） */
    private String username;

    /** 密码（BCrypt 加密存储） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像 URL */
    private String avatar;

    /** 状态：1=启用 0=禁用 */
    private Integer status;

    // ==================== 非数据库字段 ====================

    /** 角色列表（关联查询用，不存数据库） */
    @TableField(exist = false)
    private java.util.List<Role> roles;
}
