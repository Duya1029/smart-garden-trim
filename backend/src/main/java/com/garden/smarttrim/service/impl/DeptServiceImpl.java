package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.DeptDTO;
import com.garden.smarttrim.entity.SysDept;
import com.garden.smarttrim.mapper.SysDeptMapper;
import com.garden.smarttrim.service.DeptService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织架构服务实现
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptMapper deptMapper;

    @Override
    public List<DeptVO> tree() {
        List<DeptVO> all = deptMapper.selectDeptList(null, null);
        Map<Long, DeptVO> map = new LinkedHashMap<>();
        for (DeptVO vo : all) {
            map.put(vo.getId(), vo);
        }
        List<DeptVO> roots = new ArrayList<>();
        for (DeptVO vo : all) {
            Long pid = vo.getParentId();
            if (pid == null || pid == 0L || !map.containsKey(pid)) {
                roots.add(vo);
            } else {
                map.get(pid).getChildren().add(vo);
            }
        }
        sortTree(roots);
        return roots;
    }

    @Override
    public List<DeptVO> list(String deptName, String deptType) {
        return deptMapper.selectDeptList(deptName, deptType);
    }

    @Override
    public void create(DeptDTO dto) {
        checkAdmin();
        SysDept dept = new SysDept();
        dept.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        dept.setDeptName(dto.getDeptName());
        dept.setDeptType(dto.getDeptType());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setSort(dto.getSort() == null ? 0 : dto.getSort());
        dept.setStatus(1);
        deptMapper.insert(dept);
    }

    @Override
    public void update(Long id, DeptDTO dto) {
        checkAdmin();
        SysDept exist = deptMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("部门不存在");
        }
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setParentId(dto.getParentId());
        dept.setDeptName(dto.getDeptName());
        dept.setDeptType(dto.getDeptType());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setSort(dto.getSort());
        deptMapper.update(dept);
    }

    @Override
    public void delete(Long id) {
        checkAdmin();
        SysDept exist = deptMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("部门不存在");
        }
        if (deptMapper.countByParentId(id) > 0) {
            throw new BusinessException("该部门存在子部门，请先删除子部门");
        }
        if (deptMapper.countUserByDeptId(id) > 0) {
            throw new BusinessException("该部门下已关联用户，无法删除");
        }
        deptMapper.deleteById(id);
    }

    /** 递归按 sort、id 排序 */
    private void sortTree(List<DeptVO> nodes) {
        nodes.sort(Comparator.comparing(v -> v.getSort() == null ? 0 : v.getSort()));
        for (DeptVO node : nodes) {
            if (!node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
