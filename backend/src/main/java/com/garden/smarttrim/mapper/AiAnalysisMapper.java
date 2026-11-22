package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.AiAnalysis;
import org.apache.ibatis.annotations.Param;

/**
 * AI 分析结果 Mapper（SQL 见 resources/mapper/AiAnalysisMapper.xml）
 */
public interface AiAnalysisMapper {

    /** 新增分析结果 */
    int insert(AiAnalysis analysis);

    /** 查询某任务最近一次分析结果 */
    AiAnalysis selectLatestByTaskId(@Param("taskId") Long taskId);
}
