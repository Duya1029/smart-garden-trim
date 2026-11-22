package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求 DTO
 */
@Data
public class LoginDTO implements Serializable {

    /** 登录账号 */
    @NotBlank(message = "账号不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
