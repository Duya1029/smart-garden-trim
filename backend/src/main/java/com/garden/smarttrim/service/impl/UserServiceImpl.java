package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.UserDTO;
import com.garden.smarttrim.entity.SysUser;
import com.garden.smarttrim.mapper.SysClientMapper;
import com.garden.smarttrim.mapper.SysUserMapper;
import com.garden.smarttrim.service.UserService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.ClientVO;
import com.garden.smarttrim.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账号管理服务实现（管理员）
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysClientMapper clientMapper;

    /** 密码编码器（新增 / 修改密码时加密） */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<UserVO> page(String username, String realName, String role,
                                   Long deptId, Integer status, Integer pageNum, Integer pageSize) {
        checkAdmin();
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (num - 1) * size;
        long total = userMapper.countUserPage(username, realName, role, deptId, status);
        List<UserVO> list = userMapper.selectUserPage(username, realName, role, deptId, status, offset, size);
        return PageResult.of(total, list);
    }

    @Override
    public UserVO detail(Long id) {
        checkAdmin();
        UserVO vo = userMapper.selectUserVoById(id);
        if (vo == null) {
            throw new BusinessException("账号不存在");
        }
        return vo;
    }

    @Override
    public void create(UserDTO dto) {
        checkAdmin();
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException("账号已存在");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setDeptId(dto.getDeptId());
        user.setClientId(dto.getClientId());
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        userMapper.insert(user);
    }

    @Override
    public void update(Long id, UserDTO dto) {
        checkAdmin();
        SysUser exist = userMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("账号不存在");
        }
        SysUser sameName = userMapper.selectByUsername(dto.getUsername());
        if (sameName != null && !sameName.getId().equals(id)) {
            throw new BusinessException("账号已存在");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(dto.getUsername());
        // password 为空则保持原密码不变（XML 中动态跳过）
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        user.setRealName(dto.getRealName());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setDeptId(dto.getDeptId());
        user.setClientId(dto.getClientId());
        user.setStatus(dto.getStatus());
        userMapper.update(user);
    }

    @Override
    public void delete(Long id) {
        checkAdmin();
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("账号不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        checkAdmin();
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不合法");
        }
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("账号不存在");
        }
        userMapper.updateStatus(id, status);
    }

    @Override
    public List<ClientVO> clientOptions() {
        checkAdmin();
        return clientMapper.selectClientOptions();
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
