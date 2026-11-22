package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 枝条识别请求 DTO
 */
@Data
public class AiAnalyzeDTO implements Serializable {

    /** 现场照片地址 */
    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    /** 树木 ID，用于取树种等先验信息 */
    private Long treeId;

    /** 任务 ID，用于结果关联 */
    private Long taskId;

    /** 树种，缺省时由 treeId 反查 */
    private String species;

    /** 养护人员补充说明 */
    private String remark;
}
