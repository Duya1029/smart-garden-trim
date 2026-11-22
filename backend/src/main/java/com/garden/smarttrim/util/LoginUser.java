package com.garden.smarttrim.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前登录用户信息（由 JWT 解析得到，存放于请求级 ThreadLocal）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    /** 用户ID */
    private Long userId;
    /** 登录账号 */
    private String username;
    /** 真实姓名 */
    private String realName;
    /** 角色：ADMIN / WORKER / CLIENT */
    private String role;
    /** 所属部门/养护队ID */
    private Long deptId;
    /** 所属客户ID */
    private Long clientId;
}
