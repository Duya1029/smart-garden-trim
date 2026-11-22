package com.garden.smarttrim.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 *
 * <p>统一扫描 Mapper 接口；SQL 语句全部维护在 resources/mapper/**\/*.xml 中，
 * 并通过 application.yml 的 mybatis.mapper-locations 指定。</p>
 */
@Configuration
@MapperScan("com.garden.smarttrim.mapper")
public class MyBatisConfig {
}
