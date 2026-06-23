package com.library.module.reader.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 读者实体
 * <p>
 * 读者（Reader）与系统用户（User）的区别：
 * - User:   系统后台管理账号（管理员、图书管理员）
 * - Reader: 图书馆服务对象（学生、教师等借阅者）
 * </p>
 * 读者不登后台系统，通过 readerNo（借阅证号）标识。
 */
@Data
@TableName("reader")
public class Reader {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 读者编号（借阅证号，唯一） */
    private String readerNo;

    /** 真实姓名 */
    private String realName;

    /** 性别：1=男 2=女 */
    private Integer gender;

    /** 身份证号 */
    private String idCard;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 地址 */
    private String address;

    /** 读者类型：1=学生 2=教师 3=校外 */
    private Integer readerType;

    /** 最大可借数量 */
    private Integer maxBorrowCount;

    /** 最大可借天数 */
    private Integer maxBorrowDays;

    /** 状态：1=正常 0=停用 */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
