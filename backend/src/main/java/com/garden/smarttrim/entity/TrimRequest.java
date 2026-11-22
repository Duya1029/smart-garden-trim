package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪需求表 trim_request（客户发起）
 */
@Data
public class TrimRequest implements Serializable {

    /** 主键 */
    private Long id;
    /** 需求单号 */
    private String requestNo;
    /** 发起客户ID */
    private Long clientId;
    /** 需修剪的树木ID */
    private Long treeId;
    /** 期望修剪类型 */
    private String trimType;
    /** 客户预约时间 */
    private LocalDateTime appointTime;
    /** 修剪地点 */
    private String location;
    /** 备注 */
    private String remark;
    /** 状态：PENDING/AUDITED/DISPATCHED/COMPLETED/REJECTED/CANCELLED */
    private String status;
    /** 审核人ID */
    private Long auditorId;
    /** 审核意见 */
    private String auditRemark;
    /** 审核时间 */
    private LocalDateTime auditTime;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
