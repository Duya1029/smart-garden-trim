package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树木生长档案 VO（基础信息 + 修剪记录时间线 + 评分趋势）
 */
@Data
public class TreeArchiveVO implements Serializable {

    /** 树木基础信息 */
    private TreeInfo tree;
    /** 修剪记录列表 */
    private List<TreeArchiveRecordVO> records = new ArrayList<>();
    /** 评分趋势 */
    private List<ScorePointVO> scoreTrend = new ArrayList<>();

    /**
     * 树木基础信息（生长档案中仅需展示的少量字段）
     */
    @Data
    public static class TreeInfo implements Serializable {
        /** 树木ID */
        private Long id;
        /** 树木编号 */
        private String treeCode;
        /** 树种 */
        private String species;
        /** 树龄（年） */
        private Integer treeAge;
    }
}
