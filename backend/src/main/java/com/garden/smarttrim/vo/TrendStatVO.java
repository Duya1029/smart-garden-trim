package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 修剪趋势统计 VO
 */
@Data
public class TrendStatVO implements Serializable {

    /** 日期（yyyy-MM-dd） */
    private String date;
    /** 当日修剪记录数 */
    private Long count;
}
