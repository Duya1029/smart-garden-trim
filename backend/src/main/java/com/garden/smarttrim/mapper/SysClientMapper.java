package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.SysClient;
import com.garden.smarttrim.vo.ClientVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户主体 Mapper（SQL 见 resources/mapper/SysClientMapper.xml）
 */
public interface SysClientMapper {

    /** 客户分页总数 */
    long countClientPage(@Param("clientName") String clientName);

    /** 客户分页列表 */
    List<ClientVO> selectClientPage(@Param("clientName") String clientName,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    /** 客户下拉列表（全量） */
    List<ClientVO> selectClientOptions();

    /** 根据ID查询客户 VO */
    ClientVO selectClientVoById(@Param("id") Long id);

    /** 根据ID查询客户实体 */
    SysClient selectById(@Param("id") Long id);

    /** 新增客户 */
    int insert(SysClient client);

    /** 修改客户 */
    int update(SysClient client);

    /** 删除客户 */
    int deleteById(@Param("id") Long id);

    /** 客户总数（统计看板） */
    long countAll();
}
