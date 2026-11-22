package com.garden.smarttrim.config;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.util.JwtUtil;
import com.garden.smarttrim.util.LoginUser;
import com.garden.smarttrim.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器
 *
 * <p>解析请求头中的令牌并写入 SecurityUtil；请求结束后清理 ThreadLocal，避免线程复用串号。</p>
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String headerValue = request.getHeader(jwtUtil.getHeader());
        String token = jwtUtil.resolveToken(headerValue);
        if (token == null || token.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        LoginUser loginUser = jwtUtil.parseToken(token);
        SecurityUtil.set(loginUser);

        // 角色级拦截：/admin/** 仅管理员可访问
        if (request.getRequestURI().contains("/admin/") && !SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        SecurityUtil.clear();
    }
}
