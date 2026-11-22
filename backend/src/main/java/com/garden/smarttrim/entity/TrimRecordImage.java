package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修剪记录图片表 trim_record_image
 */
@Data
public class TrimRecordImage implements Serializable {

    /** 主键 */
    private Long id;
    /** 关联记录ID */
    private Long recordId;
    /** 关联任务ID（冗余，便于按任务查图） */
    private Long taskId;
    /** 图片类型：SCENE/BEFORE/AFTER/AI_MARKED */
    private String imageType;
    /** 图片地址 */
    private String imageUrl;
    /** 创建时间 */
    private LocalDateTime createTime;
}
