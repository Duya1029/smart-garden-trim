package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.EvaluateDTO;
import com.garden.smarttrim.dto.TrimRecordQueryDTO;
import com.garden.smarttrim.service.TrimRecordService;
import com.garden.smarttrim.vo.ScoreTrendVO;
import com.garden.smarttrim.vo.TrimRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 修剪记录模块 /trim-records
 */
@RestController
@RequestMapping("/trim-records")
@RequiredArgsConstructor
public class TrimRecordController {

    private final TrimRecordService trimRecordService;

    /** 记录分页查询（管理员） */
    @GetMapping("/page")
    public Result<PageResult<TrimRecordVO>> page(TrimRecordQueryDTO query) {
        return Result.ok(trimRecordService.page(query));
    }

    /** 我的记录（客户） */
    @GetMapping("/my")
    public Result<List<TrimRecordVO>> my(@RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate) {
        return Result.ok(trimRecordService.myRecords(startDate, endDate));
    }

    /** 评分趋势（客户） */
    @GetMapping("/my-trend")
    public Result<List<ScoreTrendVO>> myTrend(@RequestParam(required = false) Integer months) {
        return Result.ok(trimRecordService.myTrend(months));
    }

    /** 记录详情 */
    @GetMapping("/{id}")
    public Result<TrimRecordVO> detail(@PathVariable Long id) {
        return Result.ok(trimRecordService.detail(id));
    }

    /** 客户评价 */
    @PostMapping("/{id}/evaluate")
    public Result<Void> evaluate(@PathVariable Long id, @Valid @RequestBody EvaluateDTO dto) {
        trimRecordService.evaluate(id, dto);
        return Result.ok();
    }
}
