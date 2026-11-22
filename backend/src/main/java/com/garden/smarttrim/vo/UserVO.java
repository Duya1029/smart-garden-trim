package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号信息 VO
 */
@Data
public class UserVO implements Serializable {

    /** 用户ID */
    private Long id;
    /** 登录账号 */
    private String username;
    /** 真实姓名 */
    private String realName;
    /** 角色：ADMIN / WORKER / CLIENT */
    private String role;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
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
    /** 状态：1 启用 / 0 停用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}
