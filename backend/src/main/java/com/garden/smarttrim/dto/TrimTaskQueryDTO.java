package com.garden.smarttrim.dto;

import com.garden.smarttrim.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修剪任务分页查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrimTaskQueryDTO extends PageQuery {

    /** 任务状态 */
    private String status;

    /** 养护人员ID */
    private Long workerId;

    /** 客户ID */
    private Long clientId;

    /** 关键词（任务单号 / 树木编号 / 位置） */
    private String keyword;
}
