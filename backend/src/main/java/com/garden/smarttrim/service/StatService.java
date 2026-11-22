package com.garden.smarttrim.service;

import com.garden.smarttrim.vo.AreaStatVO;
import com.garden.smarttrim.vo.SpeciesStatVO;
import com.garden.smarttrim.vo.StatOverviewVO;
import com.garden.smarttrim.vo.TrendStatVO;
import com.garden.smarttrim.vo.WorkerStatVO;

import java.util.List;

/**
 * 统计看板服务（管理员）
 */
public interface StatService {

    /** 总览指标 */
    StatOverviewVO overview(String startDate, String endDate);

    /** 区域分布 */
    List<AreaStatVO> area();

    /** 修剪趋势（近 N 天，缺失日期补 0） */
    List<TrendStatVO> trend(Integer days);

    /** 树种分布 */
    List<SpeciesStatVO> species();

    /** 养护人员工作量 */
    List<WorkerStatVO> worker(String startDate, String endDate);
}
