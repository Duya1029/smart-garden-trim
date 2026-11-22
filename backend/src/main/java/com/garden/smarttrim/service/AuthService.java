package com.garden.smarttrim.service;

import com.garden.smarttrim.dto.LoginDTO;
import com.garden.smarttrim.dto.PasswordDTO;
import com.garden.smarttrim.vo.LoginVO;

/**
 * 认证服务
 */
public interface AuthService {

    /** 登录 */
    LoginVO login(LoginDTO dto);

    /** 获取当前登录用户信息 */
    LoginVO info();

    /** 退出登录（后端无状态） */
    void logout();

    /** 修改密码 */
    void changePassword(PasswordDTO dto);
}
