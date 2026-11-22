package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户评分趋势 VO（按月分组）
 */
@Data
public class ScoreTrendVO implements Serializable {

    /** 月份（yyyy-MM） */
    private String date;
    /** 平均评分 */
    private Double score;
    /** 记录条数 */
    private Long count;
}
