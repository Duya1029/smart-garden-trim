package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 树木档案表 tree（一树一码）
 */
@Data
public class Tree implements Serializable {

    /** 主键 */
    private Long id;
    /** 树木编号（一树一码） */
    private String treeCode;
    /** 树种 */
    private String species;
    /** 品种 */
    private String variety;
    /** 所属区域 */
    private String area;
    /** 具体位置 */
    private String location;
    /** 经度 */
    private BigDecimal longitude;
    /** 纬度 */
    private BigDecimal latitude;
    /** 树龄（年） */
    private Integer treeAge;
    /** 栽种时间 */
    private LocalDate plantTime;
    /** 树高（米） */
    private BigDecimal height;
    /** 冠幅（米） */
    private BigDecimal crownDiameter;
    /** 健康状态：HEALTHY/SUB_HEALTHY/SICK/DEAD */
    private String healthStatus;
    /** 所属客户ID */
    private Long clientId;
    /** 归属养护队ID */
    private Long deptId;
    /** 树木照片 */
    private String imageUrl;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
