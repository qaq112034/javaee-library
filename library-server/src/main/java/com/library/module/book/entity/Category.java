package com.library.module.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书分类实体
 * <p>
 * 支持多级分类（树形结构）：
 * - parentId = 0 表示顶级分类
 * - children 存放子分类列表（非数据库字段）
 * </p>
 */
@Data
@TableName("book_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String categoryName;

    /** 父分类ID（0=顶级） */
    private Long parentId;

    /** 排序号 */
    private Integer sortOrder;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 子分类列表（非数据库字段） */
    @TableField(exist = false)
    private List<Category> children;
}
