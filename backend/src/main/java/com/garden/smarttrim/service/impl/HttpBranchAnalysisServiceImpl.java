package com.garden.smarttrim.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.config.AiProperties;
import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.service.BranchAnalysisService;
import com.garden.smarttrim.vo.AiAnalyzeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http 枝条识别策略实现（真实模型接入预留）
 *
 * <p>provider = http 时生效。POST 到 garden.ai.endpoint，携带 api-key 头（非空时），
 * 超时由 RestTemplate 统一配置（见 config/RestTemplateConfig）。
 * 返回结构遵循接口文档 8.1，可直接解析为 AiAnalyzeVO，无需改动任何业务代码。</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "garden.ai.provider", havingValue = "http")
@RequiredArgsConstructor
public class HttpBranchAnalysisServiceImpl implements BranchAnalysisService {

    private final RestTemplate aiRestTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Override
    public AiAnalyzeVO analyze(AiAnalyzeDTO dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (aiProperties.getApiKey() != null && !aiProperties.getApiKey().isBlank()) {
                headers.set("api-key", aiProperties.getApiKey());
            }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("imageUrl", dto.getImageUrl());
            body.put("treeId", dto.getTreeId());
            body.put("taskId", dto.getTaskId());
            body.put("species", dto.getSpecies());
            body.put("remark", dto.getRemark());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = aiRestTemplate.postForObject(aiProperties.getEndpoint(), request, String.class);
            if (response == null || response.isBlank()) {
                throw new BusinessException("AI 模型服务返回为空");
            }
            AiAnalyzeVO vo = objectMapper.readValue(response, AiAnalyzeVO.class);
            log.info("AI 模型服务识别完成，endpoint={}", aiProperties.getEndpoint());
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 AI 模型服务失败，endpoint={}", aiProperties.getEndpoint(), e);
            throw new BusinessException("AI 模型服务调用失败：" + e.getMessage());
        }
    }
}
