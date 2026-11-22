package com.garden.smarttrim.service;

import com.garden.smarttrim.dto.DeptDTO;
import com.garden.smarttrim.vo.DeptVO;

import java.util.List;

/**
 * 组织架构服务（管理员）
 */
public interface DeptService {

    /** 部门/养护队树 */
    List<DeptVO> tree();

    /** 部门列表（平铺） */
    List<DeptVO> list(String deptName, String deptType);

    /** 新增部门 */
    void create(DeptDTO dto);

    /** 修改部门 */
    void update(Long id, DeptDTO dto);

    /** 删除部门（存在子节点或关联用户时抛业务异常） */
    void delete(Long id);
}
