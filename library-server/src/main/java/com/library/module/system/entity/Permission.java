package com.library.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体
 * <p>
 * 采用 RBAC（Role-Based Access Control）模型：
 * 用户 → 角色 → 权限 三层关系。
 * 权限类型：
 * - menu:   菜单权限（控制左侧菜单显示）
 * - button: 按钮权限（控制页面内按钮显示，如"新增"、"删除"）
 * - api:    接口权限（控制后端 API 访问）
 * </p>
 */
@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限名称 */
    private String permName;

    /** 权限编码（如 book:add, user:delete） */
    private String permCode;

    /** 权限类型：menu / button / api */
    private String permType;

    /** 父级权限ID（0=顶级） */
    private Long parentId;

    /** 前端路由路径 */
    private String path;

    /** 图标 */
    private String icon;

    /** 排序号 */
    private Integer sortOrder;

    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================

    /** 子权限列表（树形结构用） */
    @TableField(exist = false)
    private List<Permission> children;
}
