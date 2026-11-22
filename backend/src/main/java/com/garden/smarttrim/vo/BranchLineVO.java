package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 枝条线条 VO（归一化坐标 0~1，相对于原图宽高）
 */
@Data
public class BranchLineVO implements Serializable {

    /** 起点 X（归一化） */
    private Double x1;
    /** 起点 Y（归一化） */
    private Double y1;
    /** 终点 X（归一化） */
    private Double x2;
    /** 终点 Y（归一化） */
    private Double y2;
    /** 枝条标签（如 下垂枝 / 主干） */
    private String label;
}
