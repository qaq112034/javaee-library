package com.library.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.module.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，自动获得：
 * - insert / delete / update / selectById
 * - selectList / selectPage（分页）
 * - LambdaQueryWrapper 链式查询
 * 无需写 XML 即可完成大部分 CRUD 操作。
 * </p>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 如有复杂查询（多表关联），可在此定义方法，
    // 然后在 XML 中编写 SQL。
    // 例如:
    // List<User> selectUsersWithRoles(@Param("username") String username);
}
