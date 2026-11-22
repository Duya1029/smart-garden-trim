package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 养护人员工作量统计 VO
 */
@Data
public class WorkerStatVO implements Serializable {

    /** 养护人员ID */
    private Long workerId;
    /** 养护人员姓名 */
    private String workerName;
    /** 承接任务数 */
    private Long taskCount;
    /** 修剪记录数 */
    private Long trimCount;
    /** 平均评分 */
    private Double avgScore;
}
