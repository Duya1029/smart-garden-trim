package com.garden.smarttrim.mapper;

import com.garden.smarttrim.dto.TrimTaskQueryDTO;
import com.garden.smarttrim.entity.TrimTask;
import com.garden.smarttrim.vo.TrimTaskVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 修剪任务 Mapper（SQL 见 resources/mapper/TrimTaskMapper.xml）
 */
public interface TrimTaskMapper {

    /** 任务分页总数 */
    long countTaskPage(TrimTaskQueryDTO query);

    /** 任务分页列表（联查树木 / 养护人员 / 客户） */
    List<TrimTaskVO> selectTaskPage(TrimTaskQueryDTO query);

    /** 我的任务（按养护人员过滤） */
    List<TrimTaskVO> selectMyTasks(@Param("workerId") Long workerId, @Param("status") String status);

    /** 根据ID查询任务 VO */
    TrimTaskVO selectTaskVoById(@Param("id") Long id);

    /** 根据ID查询任务实体 */
    TrimTask selectById(@Param("id") Long id);

    /** 新增任务 */
    int insert(TrimTask task);

    /** 更新状态 */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /** 改派养护人员 */
    int updateWorker(@Param("id") Long id, @Param("workerId") Long workerId);

    /** 提交记录后置为 SUBMITTED 并写完成时间 */
    int updateAfterSubmit(@Param("id") Long id);

    /** 结单置为 DONE */
    int finishTask(@Param("id") Long id);

    /** 按单号前缀统计当日流水数量 */
    long countByNoPrefix(@Param("prefix") String prefix);

    /** 任务统计（总数，支持日期过滤） */
    long countTaskTotal(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /** 任务统计（按状态集合，支持日期过滤） */
    long countTaskByStatuses(@Param("statuses") List<String> statuses,
                             @Param("startDate") String startDate,
                             @Param("endDate") String endDate);
}
