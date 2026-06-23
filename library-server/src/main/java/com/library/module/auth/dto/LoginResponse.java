package com.library.module.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 登录响应 DTO
 * <p>
 * 包含前端需要的所有登录后信息：
 * - accessToken: JWT Token（前端存储后每次请求携带）
 * - 用户基本信息（显示在页面右上角）
 * - 权限列表（前端据此显示/隐藏菜单和按钮）
 * </p>
 */
@Data
@Builder
public class LoginResponse {

    /** JWT 访问令牌 */
    private String accessToken;

    /** Token 类型（固定 Bearer） */
    private String tokenType;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 头像 */
    private String avatar;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限编码列表 */
    private List<String> permissions;
}
