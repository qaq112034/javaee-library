package com.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token 工具类
 * <p>
 * 核心功能：
 * - 生成 Token：登录成功后调用，将用户信息编码到 Token 中
 * - 验证 Token：每次请求时调用，校验 Token 是否合法有效
 * - 解析 Token：从 Token 中提取用户信息（用户名、角色等）
 * </p>
 *
 * JWT 结构（三段 Base64，用 . 分隔）：
 * Header.Payload.Signature
 * - Header:  算法类型（HS256）
 * - Payload:  存放用户数据（sub=用户名, roles=角色列表, exp=过期时间）
 * - Signature: 对前两段用密钥签名，防止篡改
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expiration;

    /**
     * 构造函数，从 application.yml 读取配置
     *
     * @param secret     Base64 编码的密钥
     * @param expiration Token 有效期（毫秒）
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        // 解码 Base64 密钥为 SecretKey 对象
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    角色列表（如 "ROLE_ADMIN,ROLE_LIBRARIAN"）
     * @return JWT 字符串
     */
    public String generateToken(Long userId, String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)                       // sub: 用户名
                .claim("userId", userId)                 // 自定义：用户ID
                .claim("roles", roles)                   // 自定义：角色
                .issuedAt(now)                           // iat: 签发时间
                .expiration(expiryDate)                  // exp: 过期时间
                .signWith(secretKey)                     // 用密钥签名
                .compact();
    }

    /**
     * 从 Token 中提取用户名（subject）
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 中提取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * 从 Token 中提取角色
     */
    public String getRolesFromToken(String token) {
        return parseClaims(token).get("roles", String.class);
    }

    /**
     * 验证 Token 是否有效
     *
     * @return true=有效, false=过期/无效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token 已过期: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT Token 格式错误: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的 JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT Token 为空: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 解析 Token 的 Payload 部分
     * <p>
     * JwtParser 会自动验证签名和过期时间，
     * 签名不匹配或已过期会抛出异常。
     * </p>
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
