package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求 DTO
 */
@Data
public class PasswordDTO implements Serializable {

    /** 原密码 */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码（6~20 位） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度需为 6~20 位")
    private String newPassword;
}
