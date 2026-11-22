package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪记录表 trim_record
 */
@Data
public class TrimRecord implements Serializable {

    /** 主键 */
    private Long id;
    /** 关联任务ID */
    private Long taskId;
    /** 树木ID */
    private Long treeId;
    /** 养护人员ID */
    private Long workerId;
    /** 客户ID */
    private Long clientId;
    /** 实际修剪类型 */
    private String trimType;
    /** 实际修剪时间 */
    private LocalDateTime actualTrimTime;
    /** 剪除枝条数 */
    private Integer cutBranchCount;
    /** 保留枝条数 */
    private Integer keepBranchCount;
    /** 实际修剪情况说明 */
    private String workContent;
    /** 采纳的 AI 建议文本 */
    private String aiSuggestion;
    /** 客户评分 1~5 */
    private Integer score;
    /** 客户评语 */
    private String comment;
    /** 评价时间 */
    private LocalDateTime evaluateTime;
    /** 状态：SUBMITTED 待评价 / EVALUATED 已评价 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
