package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.TreeDTO;
import com.garden.smarttrim.dto.TreeQueryDTO;
import com.garden.smarttrim.service.TreeService;
import com.garden.smarttrim.vo.TreeArchiveVO;
import com.garden.smarttrim.vo.TreeVO;
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
 * 树木档案模块 /trees
 */
@RestController
@RequestMapping("/trees")
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;

    /** 树木分页查询（管理员 / 养护人员） */
    @GetMapping("/page")
    public Result<PageResult<TreeVO>> page(TreeQueryDTO query) {
        return Result.ok(treeService.page(query));
    }

    /** 名下树木（客户） */
    @GetMapping("/my")
    public Result<List<TreeVO>> my(@RequestParam(required = false) String keyword) {
        return Result.ok(treeService.myTrees(keyword));
    }

    /** 生长档案 */
    @GetMapping("/{id}/archive")
    public Result<TreeArchiveVO> archive(@PathVariable Long id) {
        return Result.ok(treeService.archive(id));
    }

    /** 树木详情 */
    @GetMapping("/{id}")
    public Result<TreeVO> detail(@PathVariable Long id) {
        return Result.ok(treeService.detail(id));
    }

    /** 新增树木（管理员） */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TreeDTO dto) {
        treeService.create(dto);
        return Result.ok();
    }

    /** 修改树木（管理员） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TreeDTO dto) {
        treeService.update(id, dto);
        return Result.ok();
    }

    /** 删除树木（管理员） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        treeService.delete(id);
        return Result.ok();
    }
}
