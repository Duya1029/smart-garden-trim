package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪任务表 trim_task
 */
@Data
public class TrimTask implements Serializable {

    /** 主键 */
    private Long id;
    /** 任务单号 */
    private String taskNo;
    /** 来源需求ID（管理员直接派单时为空） */
    private Long requestId;
    /** 树木ID */
    private Long treeId;
    /** 养护人员ID */
    private Long workerId;
    /** 对应客户ID */
    private Long clientId;
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
    /** 更新时间 */
    private LocalDateTime updateTime;
}
