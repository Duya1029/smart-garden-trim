package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.service.StatService;
import com.garden.smarttrim.vo.AreaStatVO;
import com.garden.smarttrim.vo.SpeciesStatVO;
import com.garden.smarttrim.vo.StatOverviewVO;
import com.garden.smarttrim.vo.TrendStatVO;
import com.garden.smarttrim.vo.WorkerStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计看板模块 /stat（管理员）
 */
@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    /** 总览指标 */
    @GetMapping("/overview")
    public Result<StatOverviewVO> overview(@RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        return Result.ok(statService.overview(startDate, endDate));
    }

    /** 区域分布 */
    @GetMapping("/area")
    public Result<List<AreaStatVO>> area() {
        return Result.ok(statService.area());
    }

    /** 修剪趋势 */
    @GetMapping("/trend")
    public Result<List<TrendStatVO>> trend(@RequestParam(required = false) Integer days) {
        return Result.ok(statService.trend(days));
    }

    /** 树种分布 */
    @GetMapping("/species")
    public Result<List<SpeciesStatVO>> species() {
        return Result.ok(statService.species());
    }

    /** 养护人员工作量 */
    @GetMapping("/worker")
    public Result<List<WorkerStatVO>> worker(@RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate) {
        return Result.ok(statService.worker(startDate, endDate));
    }
}
