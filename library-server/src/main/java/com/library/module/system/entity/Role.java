package com.library.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色实体
 */
@Data
@TableName("sys_role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称（如：超级管理员） */
    private String roleName;

    /** 角色编码（如：ROLE_ADMIN） */
    private String roleCode;

    /** 角色描述 */
    private String description;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
