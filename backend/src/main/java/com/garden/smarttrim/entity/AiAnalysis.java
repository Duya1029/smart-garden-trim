package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 分析结果表 ai_analysis
 */
@Data
public class AiAnalysis implements Serializable {

    /** 主键 */
    private Long id;
    /** 关联任务ID */
    private Long taskId;
    /** 关联树木ID */
    private Long treeId;
    /** 树种（识别时使用的先验信息） */
    private String species;
    /** 原始现场照片 */
    private String imageUrl;
    /** 标注合成图（红线/绿线已绘制） */
    private String markedImageUrl;
    /** 建议剪除枝条 JSON：[{x1,y1,x2,y2,label}] */
    private String cutBranches;
    /** 建议保留枝条 JSON */
    private String keepBranches;
    /** 修剪建议文本 */
    private String suggestion;
    /** 置信度 0~1 */
    private BigDecimal confidence;
    /** 是否 Mock 数据：1 是 / 0 否 */
    private Integer isMock;
    /** 模型版本标识 */
    private String modelVersion;
    /** 创建时间 */
    private LocalDateTime createTime;
}
