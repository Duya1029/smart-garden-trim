package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.EvaluateDTO;
import com.garden.smarttrim.dto.TrimRecordQueryDTO;
import com.garden.smarttrim.vo.ScoreTrendVO;
import com.garden.smarttrim.vo.TrimRecordVO;

import java.util.List;

/**
 * 修剪记录服务
 */
public interface TrimRecordService {

    /** 记录分页查询（管理员） */
    PageResult<TrimRecordVO> page(TrimRecordQueryDTO query);

    /** 记录详情 */
    TrimRecordVO detail(Long id);

    /** 我的记录（客户） */
    List<TrimRecordVO> myRecords(String startDate, String endDate);

    /** 客户评价 */
    void evaluate(Long id, EvaluateDTO dto);

    /** 客户评分趋势（按月分组） */
    List<ScoreTrendVO> myTrend(Integer months);
}
