package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 评分趋势数据点 VO（生长档案 scoreTrend 元素）
 */
@Data
public class ScorePointVO implements Serializable {

    /** 日期（yyyy-MM-dd） */
    private LocalDate date;
    /** 评分 */
    private Integer score;
}
