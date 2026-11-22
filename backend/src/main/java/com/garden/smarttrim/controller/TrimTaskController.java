package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.TrimTaskQueryDTO;
import com.garden.smarttrim.dto.TrimTaskSubmitDTO;
import com.garden.smarttrim.service.TrimTaskService;
import com.garden.smarttrim.vo.TrimTaskVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 修剪任务模块 /trim-tasks
 */
@RestController
@RequestMapping("/trim-tasks")
@RequiredArgsConstructor
public class TrimTaskController {

    private final TrimTaskService trimTaskService;

    /** 任务分页查询（管理员） */
    @GetMapping("/page")
    public Result<PageResult<TrimTaskVO>> page(TrimTaskQueryDTO query) {
        return Result.ok(trimTaskService.page(query));
    }

    /** 我的任务（养护人员） */
    @GetMapping("/my")
    public Result<List<TrimTaskVO>> my(@RequestParam(required = false) String status) {
        return Result.ok(trimTaskService.myTasks(status));
    }

    /** 任务详情 */
    @GetMapping("/{id}")
    public Result<TrimTaskVO> detail(@PathVariable Long id) {
        return Result.ok(trimTaskService.detail(id));
    }

    /** 接单（养护人员） */
    @PutMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable Long id) {
        trimTaskService.accept(id);
        return Result.ok();
    }

    /** 开始修剪（养护人员） */
    @PutMapping("/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        trimTaskService.start(id);
        return Result.ok();
    }

    /** 提交修剪记录（养护人员） */
    @PutMapping("/{id}/submit")
    public Result<Map<String, Object>> submit(@PathVariable Long id, @Valid @RequestBody TrimTaskSubmitDTO dto) {
        return Result.ok(trimTaskService.submit(id, dto));
    }

    /** 改派任务（管理员） */
    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @RequestParam Long workerId) {
        trimTaskService.assign(id, workerId);
        return Result.ok();
    }

    /** 结单（管理员） */
    @PutMapping("/{id}/finish")
    public Result<Void> finish(@PathVariable Long id) {
        trimTaskService.finish(id);
        return Result.ok();
    }
}
