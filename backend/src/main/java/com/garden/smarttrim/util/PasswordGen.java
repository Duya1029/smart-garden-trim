package com.garden.smarttrim.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 密文生成工具
 *
 * <p>用于在生产环境直接往数据库写入加密后的密码，命令行运行：</p>
 * <pre>
 *   java -cp smart-garden-trim.jar com.garden.smarttrim.util.PasswordGen 123456
 * </pre>
 * <p>输出示例： {@code $$2a$$10$$...}（注意转义 $ 符号）</p>
 */
public final class PasswordGen {

    private PasswordGen() {
    }

    public static void main(String[] args) {
        String raw = (args != null && args.length > 0) ? args[0] : "123456";
        String encoded = new BCryptPasswordEncoder().encode(raw);
        System.out.println("明文：" + raw);
        System.out.println("BCrypt 密文：" + encoded);
        System.out.println("校验结果：" + new BCryptPasswordEncoder().matches(raw, encoded));
    }
}
