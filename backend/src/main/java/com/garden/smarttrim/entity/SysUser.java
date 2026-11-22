package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号表 sys_user
 */
@Data
public class SysUser implements Serializable {

    /** 主键 */
    private Long id;
    /** 登录账号 */
    private String username;
    /** 密码（BCrypt 密文） */
    private String password;
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
    /** 所属客户ID */
    private Long clientId;
    /** 状态：1 启用 / 0 停用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
