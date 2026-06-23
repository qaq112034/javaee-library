package com.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * <p>
 * 继承 OncePerRequestFilter 确保每个请求只执行一次该过滤器。
 * </p>
 *
 * 执行流程：
 * <pre>
 * 请求 → 提取 Header 中的 Token → 验证 Token
 *   ├── 有效 → 构建 Authentication 对象 → 存入 SecurityContext → 放行
 *   └── 无效/缺失 → 不设置 Authentication → 放行（由后续安全规则拦截）
 * </pre>
 *
 * 关键设计：即使 Token 缺失或无效也不在此处报错，
 * 而是放行交给 Spring Security 的权限校验去拦截。
 * 这样登录接口（配置为 permitAll）无需特殊处理。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.header}")
    private String header;              // 默认: Authorization

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;         // 默认: "Bearer "

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头中提取 Token
        String token = extractToken(request);

        // 2. 如果存在 Token 且验证有效
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 2.1 从 Token 中获取用户名和角色
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String roles = jwtTokenProvider.getRolesFromToken(token);

            // 2.2 将角色字符串转换为 Spring Security 的 GrantedAuthority 列表
            List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .filter(StringUtils::hasText)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 2.3 构建认证令牌
            //     因为 Token 已验证，直接标记为已认证（不需要密码）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // 2.4 存入 SecurityContext（后续的 Controller 可以通过 SecurityContextHolder 获取）
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT 认证成功: username={}, roles={}", username, roles);
        }

        // 3. 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP 请求头中提取 JWT Token
     *
     * @return Token 字符串（去除前缀），或 null（如果不存在）
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(header);
        // 检查是否以 "Bearer " 开头
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
}
