package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户主体表 sys_client（物业 / 果园等客户实体）
 */
@Data
public class SysClient implements Serializable {

    /** 主键 */
    private Long id;
    /** 客户名称（物业/果园） */
    private String clientName;
    /** 联系人 */
    private String contactName;
    /** 联系电话 */
    private String contactPhone;
    /** 地址 */
    private String address;
    /** 关联的登录账号ID（sys_user.id） */
    private Long userId;
    /** 状态：1 正常 / 0 停用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
