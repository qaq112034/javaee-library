package com.library.module.borrow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录实体
 * <p>
 * 状态流转：
 * <pre>
 * 借书 → status=1（借阅中）
 *   ├── 正常归还 → status=2（已归还）
 *   ├── 逾期未还 → status=3（已逾期，定时任务自动更新）
 *   └── 续借     → renew_count+1，due_date延长
 * </pre>
 *
 * 业务规则：
 * - 同一读者对同一本书在还之前不能重复借阅
 * - 续借最多 2 次
 * - 逾期后不能续借
 * </p>
 */
@Data
@TableName("borrow_record")
public class BorrowRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 读者ID */
    private Long readerId;

    /** 图书ID */
    private Long bookId;

    /** 借阅日期 */
    private LocalDateTime borrowDate;

    /** 应还日期 */
    private LocalDateTime dueDate;

    /** 实际归还日期 */
    private LocalDateTime returnDate;

    /**
     * 状态
     * 1 = 借阅中
     * 2 = 已归还
     * 3 = 已逾期
     */
    private Integer status;

    /** 续借次数（最多2次） */
    private Integer renewCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ==================== 非数据库字段（关联查询/展示用） ====================

    /** 读者姓名 */
    @TableField(exist = false)
    private String readerName;

    /** 读者编号 */
    @TableField(exist = false)
    private String readerNo;

    /** 书名 */
    @TableField(exist = false)
    private String bookTitle;

    /** 图书 ISBN */
    @TableField(exist = false)
    private String bookIsbn;
}
