package com.library.module.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.module.system.entity.User;
import com.library.module.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 * <p>
 * @PreAuthorize 注解说明：
 * - hasAuthority('user:list'):  拥有 user:list 权限
 * - hasRole('ROLE_ADMIN'):      拥有 ROLE_ADMIN 角色
 * </p>
 */
@Tag(name = "用户管理", description = "系统用户的增删改查")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> page = userService.pageUsers(current, size, keyword);
        return Result.ok(PageResult.from(page));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getUserById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result<Void> add(@RequestBody User user) {
        userService.addUser(user);
        return Result.okMsg("用户创建成功");
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.okMsg("用户更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.okMsg("用户删除成功");
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.get("password"));
        return Result.okMsg("密码重置成功");
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.okMsg("状态更新成功");
    }
}
