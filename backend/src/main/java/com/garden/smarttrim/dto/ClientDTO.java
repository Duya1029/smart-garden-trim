package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户主体新增 / 修改请求 DTO
 */
@Data
public class ClientDTO implements Serializable {

    /** 客户名称（物业/果园） */
    @NotBlank(message = "客户名称不能为空")
    private String clientName;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 地址 */
    private String address;

    /** 关联的登录账号ID */
    private Long userId;

    /** 状态：1 正常 / 0 停用 */
    private Integer status;
}
