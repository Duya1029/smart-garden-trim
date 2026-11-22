package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 树木档案 VO
 */
@Data
public class TreeVO implements Serializable {

    /** 树木ID */
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
    /** 客户名称 */
    private String clientName;
    /** 归属养护队ID */
    private Long deptId;
    /** 养护队名称 */
    private String deptName;
    /** 树木照片 */
    private String imageUrl;
    /** 最近修剪时间 */
    private LocalDateTime lastTrimTime;
    /** 累计修剪次数 */
    private Integer trimCount;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
