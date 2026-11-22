package com.garden.smarttrim.util;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;

/**
 * 登录用户上下文工具类
 *
 * <p>JwtInterceptor 在请求进入时写入、请求结束时清理；
 * Service / Controller 层可直接通过静态方法获取当前登录用户。</p>
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    /** 角色：管理员 */
    public static final String ROLE_ADMIN = "ADMIN";
    /** 角色：养护人员 */
    public static final String ROLE_WORKER = "WORKER";
    /** 角色：客户 */
    public static final String ROLE_CLIENT = "CLIENT";

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    /** 获取当前登录用户，未登录返回 null */
    public static LoginUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /** 获取当前登录用户，未登录抛出 401 */
    public static LoginUser getRequired() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    public static Long getUserId() {
        LoginUser user = get();
        return user == null ? null : user.getUserId();
    }

    public static String getRole() {
        LoginUser user = get();
        return user == null ? null : user.getRole();
    }

    public static Long getDeptId() {
        LoginUser user = get();
        return user == null ? null : user.getDeptId();
    }

    public static Long getClientId() {
        LoginUser user = get();
        return user == null ? null : user.getClientId();
    }

    public static boolean isAdmin() {
        return ROLE_ADMIN.equals(getRole());
    }

    public static boolean isWorker() {
        return ROLE_WORKER.equals(getRole());
    }

    public static boolean isClient() {
        return ROLE_CLIENT.equals(getRole());
    }

    /** 当前登录客户ID（非客户角色返回 null），用于数据范围过滤 */
    public static Long currentClientId() {
        return isClient() ? getClientId() : null;
    }
}
