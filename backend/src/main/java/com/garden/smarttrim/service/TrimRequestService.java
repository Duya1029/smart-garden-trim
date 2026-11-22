package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.TrimAuditDTO;
import com.garden.smarttrim.dto.TrimRequestDTO;
import com.garden.smarttrim.vo.TrimRequestVO;

import java.util.List;
import java.util.Map;

/**
 * 修剪需求服务
 */
public interface TrimRequestService {

    /** 客户发起需求，返回 {requestId, requestNo} */
    Map<String, Object> create(TrimRequestDTO dto);

    /** 需求分页查询（管理员） */
    PageResult<TrimRequestVO> page(String status, Long clientId, String keyword,
                                   Integer pageNum, Integer pageSize);

    /** 我的需求（客户） */
    List<TrimRequestVO> myRequests(String status);

    /** 审核派单（管理员），返回 {taskId, taskNo}；驳回时返回 null */
    Map<String, Object> audit(Long id, TrimAuditDTO dto);

    /** 取消需求（客户） */
    void cancel(Long id);

    /** 删除需求（管理员） */
    void delete(Long id);
}
