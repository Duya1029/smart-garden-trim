package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 枝条识别结果 VO
 */
@Data
public class AiAnalyzeVO implements Serializable {

    /** 分析结果ID */
    private Long analysisId;
    /** 关联任务ID */
    private Long taskId;
    /** 关联树木ID */
    private Long treeId;
    /** 原始现场照片 */
    private String imageUrl;
    /** 标注合成图 */
    private String markedImageUrl;
    /** 置信度 0~1 */
    private Double confidence;
    /** 是否 Mock 数据 */
    private Boolean isMock;
    /** 模型版本标识 */
    private String modelVersion;
    /** 修剪建议文本 */
    private String suggestion;
    /** 建议剪除枝条（红线） */
    private List<BranchLineVO> cutBranches = new ArrayList<>();
    /** 建议保留枝条（绿线） */
    private List<BranchLineVO> keepBranches = new ArrayList<>();
    /** 创建时间 */
    private LocalDateTime createTime;
}
