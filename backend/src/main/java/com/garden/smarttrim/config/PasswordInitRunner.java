package com.garden.smarttrim.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 初始密码加密器
 *
 * <p>初始化脚本 sql/init_data.sql 中的演示账号密码为明文 123456，
 * 应用首次启动时由本类统一转换为 BCrypt 密文入库，避免在 SQL 中出现
 * 无法校对的伪造密文。已是 BCrypt 格式（$2a/$2b/$2y 开头）的记录不会被重复处理。</p>
 *
 * <p>数据库不可用时仅打印告警，不阻断应用启动。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordInitRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, username, password FROM sys_user WHERE password IS NOT NULL");
            int encoded = 0;
            for (Map<String, Object> row : rows) {
                String raw = String.valueOf(row.get("password"));
                if (raw.startsWith("$2a$") || raw.startsWith("$2b$") || raw.startsWith("$2y$")) {
                    continue;
                }
                jdbcTemplate.update("UPDATE sys_user SET password = ? WHERE id = ?",
                        ENCODER.encode(raw), row.get("id"));
                encoded++;
                log.info("账号 [{}] 初始密码已加密入库", row.get("username"));
            }
            if (encoded > 0) {
                log.info("初始密码加密完成，共处理 {} 个账号", encoded);
            }
        } catch (Exception e) {
            log.warn("初始密码加密跳过（数据库未就绪或表结构不存在）：{}", e.getMessage());
        }
    }
}
