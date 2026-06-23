package com.library.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.module.system.entity.Permission;
import com.library.module.system.entity.Role;
import com.library.module.system.mapper.PermissionMapper;
import com.library.module.system.mapper.RoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理", description = "系统角色的增删改查")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Operation(summary = "查询所有角色")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Role>> list() {
        return Result.ok(roleMapper.selectList(null));
    }

    @Operation(summary = "查询角色拥有的权限")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Permission>> getPermissions(@PathVariable Long id) {
        return Result.ok(permissionMapper.selectPermissionsByRoleId(id));
    }

    @Operation(summary = "查询所有权限（树形结构）")
    @GetMapping("/permissions/tree")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSortOrder));
        // 构建树形结构
        List<Permission> tree = buildTree(allPermissions, 0L);
        return Result.ok(tree);
    }

    /**
     * 递归构建权限树
     */
    private List<Permission> buildTree(List<Permission> all, Long parentId) {
        return all.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .peek(p -> p.setChildren(buildTree(all, p.getId())))
                .toList();
    }
}
