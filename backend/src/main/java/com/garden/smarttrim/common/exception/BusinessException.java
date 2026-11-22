package com.garden.smarttrim.common.exception;

import com.garden.smarttrim.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * <p>Service 层校验不通过时抛出，由 GlobalExceptionHandler 统一转换为 Result。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 响应状态码 */
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.ERROR.getCode();
    }
}
