package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 修剪记录 VO
 */
@Data
public class TrimRecordVO implements Serializable {

    /** 记录ID */
    private Long id;
    /** 关联任务ID */
    private Long taskId;
    /** 任务单号 */
    private String taskNo;
    /** 树木ID */
    private Long treeId;
    /** 树木编号 */
    private String treeCode;
    /** 树种 */
    private String species;
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
    /** 图片列表 */
    private List<TrimRecordImageVO> images = new ArrayList<>();
    /** 创建时间 */
    private LocalDateTime createTime;
}
