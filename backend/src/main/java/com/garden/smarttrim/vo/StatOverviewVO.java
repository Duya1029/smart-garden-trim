package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计总览 VO
 */
@Data
public class StatOverviewVO implements Serializable {

    /** 树木总数 */
    private Long treeTotal;
    /** 已修剪树木数 */
    private Long trimmedCount;
    /** 任务总数 */
    private Long taskTotal;
    /** 已完成任务数 */
    private Long taskDone;
    /** 待处理任务数 */
    private Long taskPending;
    /** 进行中任务数 */
    private Long taskProcessing;
    /** 完成率（百分比，保留两位小数） */
    private Double completionRate;
    /** 平均评分（保留两位小数） */
    private Double avgScore;
    /** 客户总数 */
    private Long clientTotal;
    /** 养护人员总数 */
    private Long workerTotal;
}
