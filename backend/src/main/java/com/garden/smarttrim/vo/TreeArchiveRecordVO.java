package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 生长档案中的单条修剪记录 VO
 */
@Data
public class TreeArchiveRecordVO implements Serializable {

    /** 修剪记录ID */
    private Long recordId;
    /** 实际修剪时间 */
    private LocalDateTime actualTrimTime;
    /** 修剪类型 */
    private String trimType;
    /** 客户评分 1~5 */
    private Integer score;
    /** 客户评语 */
    private String comment;
    /** 实际修剪情况说明 */
    private String workContent;
    /** 图片地址列表 */
    private List<String> images = new ArrayList<>();
}
