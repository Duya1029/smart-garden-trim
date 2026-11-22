package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 树种分布统计 VO
 */
@Data
public class SpeciesStatVO implements Serializable {

    /** 树种 */
    private String species;
    /** 数量 */
    private Long count;
}
