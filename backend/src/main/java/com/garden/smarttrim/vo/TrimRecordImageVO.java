package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 修剪记录图片 VO
 */
@Data
public class TrimRecordImageVO implements Serializable {

    /** 图片类型：SCENE/BEFORE/AFTER/AI_MARKED */
    private String imageType;
    /** 图片地址 */
    private String imageUrl;
}
