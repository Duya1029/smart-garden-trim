package com.garden.smarttrim.dto;

import com.garden.smarttrim.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 树木分页查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeQueryDTO extends PageQuery {

    /** 树木编号（模糊） */
    private String treeCode;

    /** 树种（模糊） */
    private String species;

    /** 所属区域（模糊） */
    private String area;

    /** 所属客户ID */
    private Long clientId;

    /** 健康状态 */
    private String healthStatus;

    /** 归属养护队ID */
    private Long deptId;
}
