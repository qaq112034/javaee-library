package com.library.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果封装
 * <p>
 * 所有 Controller 接口统一使用此类返回，
 * 前端可据此进行统一的响应处理。
 * </p>
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /** 状态码（200=成功，其他=失败） */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    // ==================== 静态工厂方法 ====================

    /** 成功（无数据） */
    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /** 成功（自定义消息 + 数据） */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ===== Void 返回类型专用方法（避免 JDK 21 类型推断问题） =====

    /** 成功（仅消息，无数据）—— 用于返回 Result<Void> 的方法 */
    public static Result<Void> okMsg(String message) {
        return new Result<>(200, message, null);
    }

    /** 失败（仅消息）—— 用于返回 Result<Void> 的方法 */
    public static Result<Void> fail(String message) {
        return new Result<>(500, message, null);
    }

    /** 失败（错误码 + 消息） */
    public static Result<Void> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    // 常用错误码
    public static Result<Void> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    public static Result<Void> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    public static Result<Void> notFound(String message) {
        return new Result<>(404, message, null);
    }

    public static Result<Void> badRequest(String message) {
        return new Result<>(400, message, null);
    }
}
