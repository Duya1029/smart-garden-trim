package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.ClientDTO;
import com.garden.smarttrim.vo.ClientVO;

/**
 * 客户主体服务（管理员）
 */
public interface ClientService {

    /** 客户分页查询 */
    PageResult<ClientVO> page(String clientName, Integer pageNum, Integer pageSize);

    /** 新增客户 */
    void create(ClientDTO dto);

    /** 修改客户 */
    void update(Long id, ClientDTO dto);

    /** 删除客户 */
    void delete(Long id);
}
