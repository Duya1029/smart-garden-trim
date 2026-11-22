package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.LoginDTO;
import com.garden.smarttrim.dto.PasswordDTO;
import com.garden.smarttrim.service.AuthService;
import com.garden.smarttrim.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证模块 /auth
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    /** 当前登录用户 */
    @GetMapping("/info")
    public Result<LoginVO> info() {
        return Result.ok(authService.info());
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    /** 修改密码 */
    @PostMapping("/password")
    public Result<Void> password(@Valid @RequestBody PasswordDTO dto) {
        authService.changePassword(dto);
        return Result.ok();
    }
}
