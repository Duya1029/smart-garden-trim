package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功返回 VO
 */
@Data
public class LoginVO implements Serializable {

    /** 令牌 */
    private String token;
    /** 用户ID */
    private Long userId;
    /** 登录账号 */
    private String username;
    /** 真实姓名 */
    private String realName;
    /** 角色：ADMIN / WORKER / CLIENT */
    private String role;
    /** 手机号 */
    private String phone;
    /** 头像地址 */
    private String avatar;
    /** 所属部门/养护队ID */
    private Long deptId;
    /** 部门/养护队名称 */
    private String deptName;
    /** 所属客户ID */
    private Long clientId;
    /** 客户名称 */
    private String clientName;
}
