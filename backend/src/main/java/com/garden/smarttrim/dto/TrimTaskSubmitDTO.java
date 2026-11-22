package com.garden.smarttrim.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 养护人员提交修剪记录请求 DTO
 */
@Data
public class TrimTaskSubmitDTO implements Serializable {

    /** 实际修剪类型 */
    @NotBlank(message = "修剪类型不能为空")
    private String trimType;

    /** 实际修剪时间 */
    @NotNull(message = "实际修剪时间不能为空")
    private LocalDateTime actualTrimTime;

    /** 剪除枝条数 */
    private Integer cutBranchCount;

    /** 保留枝条数 */
    private Integer keepBranchCount;

    /** 实际修剪情况说明 */
    @NotBlank(message = "修剪情况说明不能为空")
    private String workContent;

    /** 采纳的 AI 建议文本 */
    private String aiSuggestion;

    /** 图片列表 */
    @Valid
    private List<ImageItem> images;

    /**
     * 图片项
     */
    @Data
    public static class ImageItem implements Serializable {

        /** 图片地址 */
        @NotBlank(message = "图片地址不能为空")
        private String url;

        /** 图片类型：BEFORE/AFTER/SCENE/AI_MARKED */
        private String imageType;
    }
}
