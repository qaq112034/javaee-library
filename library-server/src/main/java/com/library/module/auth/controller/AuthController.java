package com.library.module.auth.controller;

import com.library.common.Result;
import com.library.module.auth.dto.LoginRequest;
import com.library.module.auth.dto.LoginResponse;
import com.library.module.auth.service.AuthService;
import com.library.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口控制器
 * <p>
 * 提供登录和获取当前用户信息的 REST API。
 * </p>
 */
@Tag(name = "认证管理", description = "登录、登出、获取用户信息")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param request 登录参数（username + password）
     * @return JWT Token + 用户信息
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.ok("登录成功", response);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 前端在刷新页面后调用此接口，恢复用户状态。
     * Token 由 JWT 过滤器自动验证。
     * </p>
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo() {
        String username = SecurityUtils.getCurrentUsername();
        Map<String, Object> info = new HashMap<>();
        info.put("username", username);
        info.put("roles", SecurityUtils.getCurrentRoles());
        info.put("permissions", SecurityUtils.getCurrentPermissions());
        return Result.ok(info);
    }

    /**
     * 退出登录（前端只需清除本地 Token 即可）
     * <p>
     * JWT 是无状态的，服务端无需做任何操作。
     * 如需要 Token 黑名单功能，可将当前 Token 加入 Redis 黑名单。
     * </p>
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 无状态 JWT：只需前端清除 Token
        // 如需黑名单，此处写入 Redis: redisTemplate.opsForSet().add("blacklist", token)
        return Result.okMsg("退出成功");
    }
}
