package com.garden.smarttrim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧园林·树木智能修剪决策平台 —— 启动类
 *
 * <p>技术栈：Spring Boot 3.2 + Spring MVC + MyBatis + SQL Server（mssql-jdbc）</p>
 * <p>服务地址与端口：application.yml 中 server.port = 8080，context-path = /api，
 * 使用内置 Tomcat 启动。</p>
 *
 * @author Smart Garden Team
 */
@SpringBootApplication
public class SmartGardenApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartGardenApplication.class, args);
        System.out.println("""
                ============================================================
                  智慧园林·树木智能修剪决策平台 启动成功
                  接口地址：http://localhost:8080/api
                ============================================================
                """);
    }
}
