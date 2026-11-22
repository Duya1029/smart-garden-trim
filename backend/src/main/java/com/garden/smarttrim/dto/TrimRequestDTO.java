package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户发起修剪需求请求 DTO
 */
@Data
public class TrimRequestDTO implements Serializable {

    /** 需修剪的树木ID（须属于当前客户） */
    @NotNull(message = "请选择树木")
    private Long treeId;

    /** 预约时间 */
    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointTime;

    /** 地点（默认取树木位置） */
    @NotBlank(message = "地点不能为空")
    private String location;

    /** 期望修剪类型 */
    private String trimType;

    /** 备注 */
    private String remark;
}
