package com.library.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.module.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询该用户拥有的所有权限
     * <p>
     * RBAC 权限模型：用户 → 角色 → 权限
     * 需要三张中间表关联：
     * sys_user → sys_user_role → sys_role → sys_role_permission → sys_permission
     * </p>
     *
     * @param userId 用户ID
     * @return 权限列表（去重）
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.perm_id " +
            "INNER JOIN sys_role r ON rp.role_id = r.id " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} " +
            "ORDER BY p.sort_order")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询该角色拥有的权限
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.perm_id " +
            "WHERE rp.role_id = #{roleId} " +
            "ORDER BY p.sort_order")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询菜单类型的权限（树形结构）
     */
    @Select("SELECT * FROM sys_permission WHERE perm_type = 'menu' ORDER BY sort_order")
    List<Permission> selectMenuPermissions();
}
