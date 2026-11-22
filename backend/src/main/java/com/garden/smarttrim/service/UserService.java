package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.UserDTO;
import com.garden.smarttrim.vo.ClientVO;
import com.garden.smarttrim.vo.UserVO;

import java.util.List;

/**
 * 账号管理服务（管理员）
 */
public interface UserService {

    /** 账号分页查询 */
    PageResult<UserVO> page(String username, String realName, String role,
                            Long deptId, Integer status, Integer pageNum, Integer pageSize);

    /** 账号详情 */
    UserVO detail(Long id);

    /** 新增账号 */
    void create(UserDTO dto);

    /** 修改账号 */
    void update(Long id, UserDTO dto);

    /** 删除账号 */
    void delete(Long id);

    /** 启用/停用 */
    void updateStatus(Long id, Integer status);

    /** 客户下拉列表 */
    List<ClientVO> clientOptions();
}
