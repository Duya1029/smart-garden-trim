package com.garden.smarttrim.mapper;

import com.garden.smarttrim.dto.TrimRecordQueryDTO;
import com.garden.smarttrim.entity.TrimRecord;
import com.garden.smarttrim.vo.ScorePointVO;
import com.garden.smarttrim.vo.ScoreTrendVO;
import com.garden.smarttrim.vo.TreeArchiveRecordVO;
import com.garden.smarttrim.vo.TrimRecordVO;
import com.garden.smarttrim.vo.TrendStatVO;
import com.garden.smarttrim.vo.WorkerStatVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 修剪记录 Mapper（SQL 见 resources/mapper/TrimRecordMapper.xml）
 */
public interface TrimRecordMapper {

    /** 记录分页总数 */
    long countRecordPage(TrimRecordQueryDTO query);

    /** 记录分页列表（联查任务 / 树木 / 养护人员 / 客户） */
    List<TrimRecordVO> selectRecordPage(TrimRecordQueryDTO query);

    /** 我的记录（按客户过滤） */
    List<TrimRecordVO> selectMyRecords(@Param("clientId") Long clientId,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);

    /** 根据ID查询记录实体 */
    TrimRecord selectById(@Param("id") Long id);

    /** 根据ID查询记录 VO */
    TrimRecordVO selectRecordVoById(@Param("id") Long id);

    /** 根据任务ID查询记录VO（任务详情用） */
    TrimRecordVO selectRecordVoByTaskId(@Param("taskId") Long taskId);

    /** 新增记录 */
    int insert(TrimRecord record);

    /** 客户评价（写分数 / 评语 / 评价时间 / 状态） */
    int evaluate(TrimRecord record);

    /** 树木生长档案：该树全部修剪记录 */
    List<TreeArchiveRecordVO> selectArchiveRecords(@Param("treeId") Long treeId);

    /** 树木生长档案：评分趋势（date, score） */
    List<ScorePointVO> selectTreeScoreTrend(@Param("treeId") Long treeId);

    /** 已修剪树木数（统计看板，去重） */
    long countTrimmedTree(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /** 平均评分（统计看板） */
    Double avgScore(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /** 每日修剪记录数（统计看板） */
    List<TrendStatVO> selectDailyTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /** 养护人员工作量统计 */
    List<WorkerStatVO> selectWorkerStat(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /** 客户评分趋势（按月分组） */
    List<ScoreTrendVO> selectClientScoreTrend(@Param("clientId") Long clientId, @Param("months") Integer months);
}
