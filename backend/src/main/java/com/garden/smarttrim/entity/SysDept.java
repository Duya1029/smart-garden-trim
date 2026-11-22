package com.garden.smarttrim.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组织架构表 sys_dept（部门 / 养护队，树形结构）
 */
@Data
public class SysDept implements Serializable {

    /** 主键 */
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
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
