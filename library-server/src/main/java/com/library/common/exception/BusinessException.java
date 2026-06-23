package com.library.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 用于在 Service 层抛出可预见的业务逻辑错误，
 * 由 GlobalExceptionHandler 统一捕获并返回给前端。
 * </p>
 *
 * 使用示例：
 * <pre>{@code
 *   if (book.getAvailableCopies() <= 0) {
 *       throw new BusinessException("该图书已全部借出，无法继续借阅");
 *   }
 * }</pre>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务错误码（可选） */
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
