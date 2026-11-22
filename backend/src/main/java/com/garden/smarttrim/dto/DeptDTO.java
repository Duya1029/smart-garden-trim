package com.garden.smarttrim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 部门新增 / 修改请求 DTO
 */
@Data
public class DeptDTO implements Serializable {

    /** 上级部门ID，0 表示顶层 */
    private Long parentId;

    /** 部门/养护队名称 */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /** 类型：DEPT 部门 / FLEET 养护队 */
    @NotBlank(message = "部门类型不能为空")
    private String deptType;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 排序号 */
    private Integer sort;
}
