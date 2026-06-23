package com.library.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.module.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询该用户拥有的角色列表
     * <p>
     * SQL 涉及 sys_user_role 和 sys_role 两张表的 JOIN 查询。
     * 使用 @Select 注解直接写 SQL，简单查询无需 XML 文件。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}
