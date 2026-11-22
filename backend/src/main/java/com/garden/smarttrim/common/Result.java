package com.garden.smarttrim.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 *
 * <p>前端约定：code = 200 视为成功；401 触发重新登录；403 提示无权限；其余弹窗显示 msg。</p>
 *
 * @param <T> 业务数据类型
 */
@Data
public class Result<T> implements Serializable {

    /** 状态码 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 业务数据 */
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功（无数据） */
    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /** 成功（自定义提示 + 数据） */
    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /** 失败（默认 500） */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    /** 失败（指定状态码） */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 失败（枚举） */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }
}
