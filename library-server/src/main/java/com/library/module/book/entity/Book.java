package com.library.module.book.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 图书实体
 * <p>
 * 核心字段说明：
 * - totalCopies:    馆藏总数（采购数量）
 * - availableCopies: 可借数量（totalCopies - 当前借出数量）
 * - borrowCount:     累计借阅次数（用于热门图书排行）
 * - status:          1=上架（可见可借） 0=下架（不可见）
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book")
public class Book extends BaseEntity {

    /** ISBN 编号（国际标准书号，唯一） */
    private String isbn;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** 出版社 */
    private String publisher;

    /** 出版日期 */
    private LocalDate publishDate;

    /** 分类ID */
    private Long categoryId;

    /** 图书简介 */
    private String description;

    /** 封面图片 URL */
    private String coverUrl;

    /** 馆藏总数 */
    private Integer totalCopies;

    /** 可借数量 */
    private Integer availableCopies;

    /** 馆藏位置 */
    private String location;

    /** 状态：1=上架 0=下架 */
    private Integer status;

    /** 累计借阅次数 */
    private Integer borrowCount;

    // ==================== 非数据库字段（关联查询） ====================

    /** 分类名称（JOIN 查询用） */
    @TableField(exist = false)
    private String categoryName;
}
