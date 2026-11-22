package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 区域分布统计 VO
 */
@Data
public class AreaStatVO implements Serializable {

    /** 所属区域 */
    private String area;
    /** 树木数量 */
    private Long treeCount;
    /** 已修剪树木数量 */
    private Long trimmedCount;
}
