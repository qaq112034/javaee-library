package com.library.module.auth.service.impl;

import com.library.common.exception.BusinessException;
import com.library.module.auth.dto.LoginRequest;
import com.library.module.auth.dto.LoginResponse;
import com.library.module.auth.service.AuthService;
import com.library.module.system.entity.User;
import com.library.module.system.mapper.UserMapper;
import com.library.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 * <p>
 * 登录流程：
 * 1. 接收用户名和密码
 * 2. 调 AuthenticationManager.authenticate() 验证（走 UserDetailsServiceImpl）
 * 3. 验证通过后查询用户信息
 * 4. 生成 JWT Token
 * 5. 组装 LoginResponse 返回
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 步骤1: Spring Security 认证
            // 内部会调用 UserDetailsServiceImpl.loadUserByUsername()
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 步骤2: 获取用户信息
            String username = authentication.getName();
            User user = userMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                            .eq(User::getUsername, username)
            );

            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 步骤3: 提取角色和权限
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> auth.startsWith("ROLE_"))
                    .collect(Collectors.toList());

            List<String> permissions = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> !auth.startsWith("ROLE_"))
                    .collect(Collectors.toList());

            // 步骤4: 生成 JWT
            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getUsername(),
                    String.join(",", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
            );

            // 步骤5: 组装响应
            log.info("用户登录成功: {}", username);
            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .avatar(user.getAvatar())
                    .roles(roles)
                    .permissions(permissions)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("登录失败: 用户名或密码错误 - {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
    }
}
