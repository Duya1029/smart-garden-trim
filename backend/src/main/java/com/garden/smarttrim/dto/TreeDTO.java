package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 树木档案新增 / 修改请求 DTO
 */
@Data
public class TreeDTO implements Serializable {

    /** 树木编号（一树一码） */
    @NotBlank(message = "树木编号不能为空")
    private String treeCode;

    /** 树种 */
    @NotBlank(message = "树种不能为空")
    private String species;

    /** 品种 */
    private String variety;

    /** 所属区域 */
    @NotBlank(message = "所属区域不能为空")
    private String area;

    /** 具体位置 */
    @NotBlank(message = "具体位置不能为空")
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
    @NotNull(message = "所属客户不能为空")
    private Long clientId;

    /** 归属养护队ID */
    private Long deptId;

    /** 树木照片 */
    private String imageUrl;

    /** 备注 */
    private String remark;
}
