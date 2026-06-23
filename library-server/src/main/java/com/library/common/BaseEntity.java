package com.library.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体类基类
 * <p>
 * 提供所有实体共用的字段：主键ID、创建时间、更新时间、逻辑删除标记。
 * 子实体类直接继承即可，无需重复定义。
 * MyBatis-Plus 会自动处理这些字段的填充。
 * </p>
 */
@Data
public abstract class BaseEntity {

    /** 主键ID（使用雪花算法自动生成） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建时间（INSERT 时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（INSERT 和 UPDATE 时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     * 0 = 未删除（正常）
     * 1 = 已删除
     * MyBatis-Plus 会自动在查询时过滤 deleted=1 的记录
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
