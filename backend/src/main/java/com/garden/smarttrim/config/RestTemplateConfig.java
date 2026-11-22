package com.garden.smarttrim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置
 *
 * <p>为 HttpBranchAnalysisServiceImpl 提供调用真实 AI 模型服务的 RestTemplate，
 * 连接与读取超时取自 garden.ai.timeout（毫秒）。</p>
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    /** AI 模型调用专用 RestTemplate（带超时配置） */
    @Bean
    public RestTemplate aiRestTemplate(AiProperties aiProperties) {
        int timeout = aiProperties.getTimeout() == null ? 10000 : aiProperties.getTimeout();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
