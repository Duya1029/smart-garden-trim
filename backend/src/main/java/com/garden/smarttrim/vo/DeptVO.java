package com.garden.smarttrim.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门 / 养护队 VO（含 children 支持树形组装）
 */
@Data
public class DeptVO implements Serializable {

    /** 部门ID */
    private Long id;
    /** 上级部门ID，0 表示顶层 */
    private Long parentId;
    /** 部门/养护队名称 */
    private String deptName;
    /** 类型：DEPT 部门 / FLEET 养护队 */
    private String deptType;
    /** 负责人 */
    private String leader;
    /** 联系电话 */
    private String phone;
    /** 排序号 */
    private Integer sort;
    /** 状态：1 正常 / 0 停用 */
    private Integer status;
    /** 子节点 */
    private List<DeptVO> children = new ArrayList<>();
}
