package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.TrimAuditDTO;
import com.garden.smarttrim.dto.TrimRequestDTO;
import com.garden.smarttrim.service.TrimRequestService;
import com.garden.smarttrim.vo.TrimRequestVO;
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
import java.util.Map;

/**
 * 修剪需求模块 /trim-requests
 */
@RestController
@RequestMapping("/trim-requests")
@RequiredArgsConstructor
public class TrimRequestController {

    private final TrimRequestService trimRequestService;

    /** 客户发起需求 */
    @PostMapping
    public Result<Map<String, Object>> create(@Valid @RequestBody TrimRequestDTO dto) {
        return Result.ok(trimRequestService.create(dto));
    }

    /** 需求分页查询（管理员） */
    @GetMapping("/page")
    public Result<PageResult<TrimRequestVO>> page(@RequestParam(required = false) Integer pageNum,
                                                  @RequestParam(required = false) Integer pageSize,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) Long clientId,
                                                  @RequestParam(required = false) String keyword) {
        return Result.ok(trimRequestService.page(status, clientId, keyword, pageNum, pageSize));
    }

    /** 我的需求（客户） */
    @GetMapping("/my")
    public Result<List<TrimRequestVO>> my(@RequestParam(required = false) String status) {
        return Result.ok(trimRequestService.myRequests(status));
    }

    /** 审核派单（管理员） */
    @PutMapping("/{id}/audit")
    public Result<Map<String, Object>> audit(@PathVariable Long id, @Valid @RequestBody TrimAuditDTO dto) {
        return Result.ok(trimRequestService.audit(id, dto));
    }

    /** 取消需求（客户） */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        trimRequestService.cancel(id);
        return Result.ok();
    }

    /** 删除需求（管理员） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trimRequestService.delete(id);
        return Result.ok();
    }
}
