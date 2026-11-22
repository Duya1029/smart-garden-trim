package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.SysUser;
import com.garden.smarttrim.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账号 Mapper（SQL 见 resources/mapper/SysUserMapper.xml）
 */
public interface SysUserMapper {

    /** 账号分页总数 */
    long countUserPage(@Param("username") String username,
                       @Param("realName") String realName,
                       @Param("role") String role,
                       @Param("deptId") Long deptId,
                       @Param("status") Integer status);

    /** 账号分页列表（联查部门 / 客户名称） */
    List<UserVO> selectUserPage(@Param("username") String username,
                                @Param("realName") String realName,
                                @Param("role") String role,
                                @Param("deptId") Long deptId,
                                @Param("status") Integer status,
                                @Param("offset") Integer offset,
                                @Param("pageSize") Integer pageSize);

    /** 根据ID查询账号 VO */
    UserVO selectUserVoById(@Param("id") Long id);

    /** 根据ID查询账号实体 */
    SysUser selectById(@Param("id") Long id);

    /** 根据登录账号查询 */
    SysUser selectByUsername(@Param("username") String username);

    /** 新增账号 */
    int insert(SysUser user);

    /** 修改账号（password 为空时由 XML 动态跳过） */
    int update(SysUser user);

    /** 修改密码 */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /** 启用/停用 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 删除账号 */
    int deleteById(@Param("id") Long id);

    /** 按角色统计数量（统计看板） */
    long countByRole(@Param("role") String role);
}
