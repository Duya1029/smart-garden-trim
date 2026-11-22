package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.TrimTaskQueryDTO;
import com.garden.smarttrim.dto.TrimTaskSubmitDTO;
import com.garden.smarttrim.vo.TrimTaskVO;

import java.util.List;
import java.util.Map;

/**
 * 修剪任务服务
 */
public interface TrimTaskService {

    /** 任务分页查询（管理员） */
    PageResult<TrimTaskVO> page(TrimTaskQueryDTO query);

    /** 我的任务（养护人员） */
    List<TrimTaskVO> myTasks(String status);

    /** 任务详情（含已提交记录） */
    TrimTaskVO detail(Long id);

    /** 接单 PENDING → ACCEPTED */
    void accept(Long id);

    /** 开始修剪 ACCEPTED → PROCESSING */
    void start(Long id);

    /** 提交修剪记录，返回 {recordId} */
    Map<String, Object> submit(Long id, TrimTaskSubmitDTO dto);

    /** 改派任务（管理员） */
    void assign(Long id, Long workerId);

    /** 结单 SUBMITTED → DONE（管理员） */
    void finish(Long id);
}
