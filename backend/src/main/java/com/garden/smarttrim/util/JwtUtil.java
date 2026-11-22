package com.garden.smarttrim.util;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 *
 * <p>负责令牌的生成与解析。令牌为无状态，服务端不保存会话，
 * 所有需要鉴权的接口由 JwtInterceptor 解析令牌后写入 SecurityUtil。</p>
 */
@Getter
@Component
public class JwtUtil {

    /** 密钥（HS256 要求不少于 32 字节） */
    @Value("${garden.jwt.secret}")
    private String secret;

    /** 过期小时数 */
    @Value("${garden.jwt.expire-hours:24}")
    private long expireHours;

    /** 携带令牌的请求头 */
    @Value("${garden.jwt.header:Authorization}")
    private String header;

    /** 令牌前缀 */
    @Value("${garden.jwt.prefix:Bearer }")
    private String prefix;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成令牌
     *
     * @param user 登录用户
     * @return JWT 字符串
     */
    public String createToken(LoginUser user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireHours * 3600_000L);
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("username", user.getUsername())
                .claim("realName", user.getRealName())
                .claim("role", user.getRole())
                .claim("deptId", user.getDeptId())
                .claim("clientId", user.getClientId())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析令牌
     *
     * @param token JWT 字符串
     * @return 登录用户信息
     * @throws BusinessException 令牌无效或已过期
     */
    public LoginUser parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            LoginUser user = new LoginUser();
            user.setUserId(Long.valueOf(claims.getSubject()));
            user.setUsername(claims.get("username", String.class));
            user.setRealName(claims.get("realName", String.class));
            user.setRole(claims.get("role", String.class));
            user.setDeptId(toLong(claims.get("deptId")));
            user.setClientId(toLong(claims.get("clientId")));
            return user;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    /**
     * 从请求头中提取令牌（自动去掉 Bearer 前缀）
     */
    public String resolveToken(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        if (prefix != null && !prefix.isBlank() && headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length()).trim();
        }
        return headerValue.trim();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
