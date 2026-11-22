package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 需求审核派单请求 DTO
 */
@Data
public class TrimAuditDTO implements Serializable {

    /** 审核结果：AUDITED（通过并派单）/ REJECTED（驳回） */
    @NotBlank(message = "审核结果不能为空")
    private String status;

    /** 派单给哪个养护人员；status=AUDITED 时必填 */
    private Long workerId;

    /** 计划修剪时间，缺省取客户预约时间 */
    private LocalDateTime planTime;

    /** 审核意见 */
    private String auditRemark;
}
