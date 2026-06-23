package com.library.module.auth.service;

import com.library.module.auth.dto.LoginRequest;
import com.library.module.auth.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求（用户名 + 密码）
     * @return 登录响应（Token + 用户信息 + 权限列表）
     */
    LoginResponse login(LoginRequest request);
}
