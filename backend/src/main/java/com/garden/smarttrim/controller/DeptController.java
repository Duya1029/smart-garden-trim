package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.DeptDTO;
import com.garden.smarttrim.service.DeptService;
import com.garden.smarttrim.vo.DeptVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织架构模块 /depts（管理员）
 */
@RestController
@RequestMapping("/depts")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /** 部门/养护队树 */
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        return Result.ok(deptService.tree());
    }

    /** 部门列表（平铺） */
    @GetMapping("/list")
    public Result<List<DeptVO>> list(@RequestParam(required = false) String deptName,
                                     @RequestParam(required = false) String deptType) {
        return Result.ok(deptService.list(deptName, deptType));
    }

    /** 新增部门 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DeptDTO dto) {
        deptService.create(dto);
        return Result.ok();
    }

    /** 修改部门 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DeptDTO dto) {
        deptService.update(id, dto);
        return Result.ok();
    }

    /** 删除部门 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return Result.ok();
    }
}
