package com.garden.smarttrim.common;

/**
 * 统一响应状态码枚举
 */
public enum ResultCode {

    /** 操作成功 */
    SUCCESS(200, "操作成功"),
    /** 未登录或令牌失效 */
    UNAUTHORIZED(401, "未登录或登录已过期，请重新登录"),
    /** 已登录但无权限 */
    FORBIDDEN(403, "无权限访问该资源"),
    /** 参数校验失败 */
    BAD_REQUEST(400, "请求参数有误"),
    /** 业务异常 / 系统异常 */
    ERROR(500, "操作失败");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
