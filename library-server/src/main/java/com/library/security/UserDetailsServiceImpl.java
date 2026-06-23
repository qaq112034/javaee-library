package com.library.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.module.system.entity.Permission;
import com.library.module.system.entity.Role;
import com.library.module.system.entity.User;
import com.library.module.system.mapper.PermissionMapper;
import com.library.module.system.mapper.RoleMapper;
import com.library.module.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 的 UserDetailsService 实现
 * <p>
 * 当调用 AuthenticationManager.authenticate() 时，
 * Spring Security 会调用此类的 loadUserByUsername() 方法加载用户信息。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return Spring Security 的 UserDetails 对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 2. 查询用户的角色列表
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());

        // 3. 查询用户的所有权限（通过 用户 → 角色 → 权限）
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());

        // 4. 构建权限列表
        //    角色用 "ROLE_" 前缀
        //    权限用 perm_code 原值
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色（如 ROLE_ADMIN）
        roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode())));

        // 添加具体权限（如 book:add, book:delete）
        permissions.forEach(perm ->
                authorities.add(new SimpleGrantedAuthority(perm.getPermCode())));

        // 5. 返回 Spring Security 的 User 对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,     // enabled
                true,                       // accountNonExpired
                true,                       // credentialsNonExpired
                true,                       // accountNonLocked
                authorities
        );
    }
}
