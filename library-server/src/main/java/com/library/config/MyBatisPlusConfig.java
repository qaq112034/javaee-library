package com.library.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置项：
 * 1. 分页插件 —— 拦截 SQL，自动追加 LIMIT 子句
 * 2. 防全表更新/删除插件 —— 阻止不带 WHERE 的 UPDATE/DELETE
 * 3. 自动填充处理器 —— INSERT/UPDATE 时自动填充 createTime/updateTime
 * </p>
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器链
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件 —— 自动识别数据库类型（MySQL）
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置最大单页查询数量（防止一次查太多数据）
        pagination.setMaxLimit(200L);
        // 溢出处理：页码超过总页数时返回第一页
        pagination.setOverflow(true);
        interceptor.addInnerInterceptor(pagination);

        // 2. 阻止全表更新/删除（安全保护）
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 自动填充处理器
     * <p>
     * 在 INSERT 时自动设置 createTime 和 updateTime；
     * 在 UPDATE 时自动更新 updateTime。
     * 实体类只需在对应字段上加 @TableField(fill = ...) 注解即可。
     * </p>
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                // 创建时间
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                // 更新时间
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                // 逻辑删除默认值
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 只更新 updateTime
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
