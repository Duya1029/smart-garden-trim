package com.garden.smarttrim.dto;

import com.garden.smarttrim.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修剪记录分页查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrimRecordQueryDTO extends PageQuery {

    /** 养护人员ID */
    private Long workerId;

    /** 客户ID */
    private Long clientId;

    /** 树木ID */
    private Long treeId;

    /** 起始日期（yyyy-MM-dd） */
    private String startDate;

    /** 结束日期（yyyy-MM-dd） */
    private String endDate;
}
