package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.TrimRequest;
import com.garden.smarttrim.vo.TrimRequestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 修剪需求 Mapper（SQL 见 resources/mapper/TrimRequestMapper.xml）
 */
public interface TrimRequestMapper {

    /** 需求分页总数 */
    long countRequestPage(@Param("status") String status,
                          @Param("clientId") Long clientId,
                          @Param("keyword") String keyword);

    /** 需求分页列表（联查树木编号 / 树种 / 客户名称） */
    List<TrimRequestVO> selectRequestPage(@Param("status") String status,
                                          @Param("clientId") Long clientId,
                                          @Param("keyword") String keyword,
                                          @Param("offset") Integer offset,
                                          @Param("pageSize") Integer pageSize);

    /** 我的需求（按客户过滤） */
    List<TrimRequestVO> selectMyRequests(@Param("clientId") Long clientId, @Param("status") String status);

    /** 根据ID查询需求实体 */
    TrimRequest selectById(@Param("id") Long id);

    /** 新增需求 */
    int insert(TrimRequest request);

    /** 审核（写状态 / 审核人 / 审核意见 / 审核时间） */
    int updateAudit(TrimRequest request);

    /** 更新状态（取消等） */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /** 删除需求 */
    int deleteById(@Param("id") Long id);

    /** 按单号前缀统计当日流水数量 */
    long countByNoPrefix(@Param("prefix") String prefix);
}
