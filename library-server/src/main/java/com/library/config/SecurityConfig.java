package com.library.config;

import com.library.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置
 * <p>
 * 安全策略：
 * 1. 关闭 CSRF（前后端分离 + JWT 无状态，无需 CSRF 防护）
 * 2. 无会话（STATELESS）—— 不创建 HttpSession
 * 3. JWT 过滤器在 UsernamePasswordAuthenticationFilter 之前执行
 * 4. 登录接口和 API 文档公开访问
 * 5. 其他请求需要认证
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 启用 @PreAuthorize 注解支持
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 安全过滤器链
     * <p>
     * Spring Security 将所有配置转换为一条过滤器链，
     * 请求依次经过各个过滤器后才到达 Controller。
     * </p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF（前后端分离 + JWT 不需要）
            .csrf(AbstractHttpConfigurer::disable)

            // 2. 无状态会话
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. 请求权限配置
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问：登录、API 文档
                .requestMatchers(
                    "/api/auth/login",
                    "/doc.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                // OPTIONS 预检请求放行
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )

            // 4. 添加 JWT 过滤器（在 UsernamePasswordAuthenticationFilter 之前）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 5. 异常处理（交给 GlobalExceptionHandler）
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, rsp, e) -> {
                    rsp.setContentType("application/json;charset=UTF-8");
                    rsp.setStatus(401);
                    rsp.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
                })
                .accessDeniedHandler((req, rsp, e) -> {
                    rsp.setContentType("application/json;charset=UTF-8");
                    rsp.setStatus(403);
                    rsp.getWriter().write("{\"code\":403,\"message\":\"权限不足\",\"data\":null}");
                })
            );

        return http.build();
    }

    /**
     * 密码编码器
     * <p>
     * BCrypt —— Spring Security 推荐的密码哈希算法，
     * 自带盐值（salt），同密码每次加密结果不同，安全性高。
     * </p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * <p>
     * 负责执行认证逻辑（验证用户名密码）。
     * 在 AuthController 的登录接口中手动调用。
     * </p>
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
