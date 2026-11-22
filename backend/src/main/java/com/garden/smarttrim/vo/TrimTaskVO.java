package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪任务 VO（详情另含 record 字段）
 */
@Data
public class TrimTaskVO implements Serializable {

    /** 任务ID */
    private Long id;
    /** 任务单号 */
    private String taskNo;
    /** 来源需求ID */
    private Long requestId;
    /** 树木ID */
    private Long treeId;
    /** 树木编号 */
    private String treeCode;
    /** 树种 */
    private String species;
    /** 所属区域 */
    private String area;
    /** 具体位置 */
    private String location;
    /** 养护人员ID */
    private Long workerId;
    /** 养护人员姓名 */
    private String workerName;
    /** 客户ID */
    private Long clientId;
    /** 客户名称 */
    private String clientName;
    /** 修剪类型 */
    private String trimType;
    /** 计划修剪时间 */
    private LocalDateTime planTime;
    /** 实际完成时间 */
    private LocalDateTime finishTime;
    /** 状态：PENDING/ACCEPTED/PROCESSING/SUBMITTED/DONE */
    private String status;
    /** 优先级 1 普通 / 2 紧急 */
    private Integer priority;
    /** 任务说明 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 已提交的修剪记录（可空） */
    private TrimRecordVO record;
}
