package com.garden.smarttrim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 枝条识别配置项（对应 application.yml 中 garden.ai）
 *
 * <p>provider = mock 时使用模拟数据；provider = http 时转发到真实模型服务，
 * 便于在不改动业务代码的前提下完成真实模型接入。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "garden.ai")
public class AiProperties {

    /** 提供方：mock / http */
    private String provider = "mock";
    /** 真实模型服务地址 */
    private String endpoint = "";
    /** 模型服务访问密钥 */
    private String apiKey = "";
    /** 超时时间（毫秒） */
    private Integer timeout = 10000;
    /** 模型版本标识 */
    private String modelVersion = "mock-v1.0";

    /** 是否使用 Mock 数据 */
    public boolean isMock() {
        return !"http".equalsIgnoreCase(provider);
    }
}
