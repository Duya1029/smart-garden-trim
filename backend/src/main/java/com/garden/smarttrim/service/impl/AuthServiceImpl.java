package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.LoginDTO;
import com.garden.smarttrim.dto.PasswordDTO;
import com.garden.smarttrim.entity.SysClient;
import com.garden.smarttrim.entity.SysDept;
import com.garden.smarttrim.entity.SysUser;
import com.garden.smarttrim.mapper.SysClientMapper;
import com.garden.smarttrim.mapper.SysDeptMapper;
import com.garden.smarttrim.mapper.SysUserMapper;
import com.garden.smarttrim.service.AuthService;
import com.garden.smarttrim.util.JwtUtil;
import com.garden.smarttrim.util.LoginUser;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;
    private final SysClientMapper clientMapper;
    private final JwtUtil jwtUtil;

    /**
     * 密码编码器。
     * 演示库中初始密码为明文 123456，首次启动时由 PasswordInitRunner 统一加密为 BCrypt 密文入库，
     * 此后所有密码校验均走 BCrypt。
     */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = userMapper.selectByUsername(dto.getUsername());
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被停用，请联系管理员");
        }
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRealName(),
                user.getRole(), user.getDeptId(), user.getClientId());
        LoginVO vo = buildVo(user);
        vo.setToken(jwtUtil.createToken(loginUser));
        log.info("用户 [{}] 登录成功", user.getUsername());
        return vo;
    }

    @Override
    public LoginVO info() {
        LoginUser loginUser = SecurityUtil.getRequired();
        SysUser user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return buildVo(user);
    }

    @Override
    public void logout() {
        // 后端无状态，无需服务端处理，前端清除本地 token 即可
        log.info("用户 [{}] 退出登录", SecurityUtil.getUserId());
    }

    @Override
    public void changePassword(PasswordDTO dto) {
        LoginUser loginUser = SecurityUtil.getRequired();
        SysUser user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        userMapper.updatePassword(user.getId(), encoder.encode(dto.getNewPassword()));
        log.info("用户 [{}] 修改密码成功", user.getUsername());
    }

    /** 组装登录返回 VO（反查部门 / 客户名称） */
    private LoginVO buildVo(SysUser user) {
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setDeptId(user.getDeptId());
        vo.setClientId(user.getClientId());
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            vo.setDeptName(dept == null ? null : dept.getDeptName());
        }
        if (user.getClientId() != null) {
            SysClient client = clientMapper.selectById(user.getClientId());
            vo.setClientName(client == null ? null : client.getClientName());
        }
        return vo;
    }
}
