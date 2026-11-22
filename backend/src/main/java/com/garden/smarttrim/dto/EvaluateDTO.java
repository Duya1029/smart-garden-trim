package com.garden.smarttrim.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户评价请求 DTO
 */
@Data
public class EvaluateDTO implements Serializable {

    /** 评分 1~5 */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低 1 分")
    @Max(value = 5, message = "评分最高 5 分")
    private Integer score;

    /** 评语 */
    private String comment;
}
