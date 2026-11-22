package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪需求 VO
 */
@Data
public class TrimRequestVO implements Serializable {

    /** 需求ID */
    private Long id;
    /** 需求单号 */
    private String requestNo;
    /** 树木ID */
    private Long treeId;
    /** 树木编号 */
    private String treeCode;
    /** 树种 */
    private String species;
    /** 发起客户ID */
    private Long clientId;
    /** 客户名称 */
    private String clientName;
    /** 预约时间 */
    private LocalDateTime appointTime;
    /** 修剪地点 */
    private String location;
    /** 期望修剪类型 */
    private String trimType;
    /** 备注 */
    private String remark;
    /** 状态：PENDING/AUDITED/DISPATCHED/COMPLETED/REJECTED/CANCELLED */
    private String status;
    /** 审核意见 */
    private String auditRemark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
