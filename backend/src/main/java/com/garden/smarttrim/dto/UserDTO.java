package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 账号新增 / 修改请求 DTO
 */
@Data
public class UserDTO implements Serializable {

    /** 登录账号 */
    @NotBlank(message = "账号不能为空")
    private String username;

    /** 密码（新增必填；修改为空则不修改密码） */
    private String password;

    /** 真实姓名 */
    @NotBlank(message = "姓名不能为空")
    private String realName;

    /** 角色：ADMIN / WORKER / CLIENT */
    @NotBlank(message = "角色不能为空")
    private String role;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 所属部门/养护队ID */
    private Long deptId;

    /** 所属客户ID */
    private Long clientId;

    /** 状态：1 启用 / 0 停用 */
    private Integer status;
}
