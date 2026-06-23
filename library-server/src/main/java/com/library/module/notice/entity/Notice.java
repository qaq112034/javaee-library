package com.library.module.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 类型：1=系统公告 2=催还通知 */
    private Integer noticeType;

    /** 发布人ID */
    private Long publisherId;

    /** 状态：1=已发布 0=草稿 */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
