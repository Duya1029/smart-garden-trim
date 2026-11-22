package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.SysDept;
import com.garden.smarttrim.vo.DeptVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 / 养护队 Mapper（SQL 见 resources/mapper/SysDeptMapper.xml）
 */
public interface SysDeptMapper {

    /** 条件查询部门列表（平铺） */
    List<DeptVO> selectDeptList(@Param("deptName") String deptName, @Param("deptType") String deptType);

    /** 根据ID查询部门 */
    SysDept selectById(@Param("id") Long id);

    /** 新增部门 */
    int insert(SysDept dept);

    /** 修改部门 */
    int update(SysDept dept);

    /** 删除部门 */
    int deleteById(@Param("id") Long id);

    /** 统计子部门数量（删除前校验） */
    long countByParentId(@Param("parentId") Long parentId);

    /** 统计关联用户数量（删除前校验） */
    long countUserByDeptId(@Param("deptId") Long deptId);
}
