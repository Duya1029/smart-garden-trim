package com.garden.smarttrim.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.entity.AiAnalysis;
import com.garden.smarttrim.entity.Tree;
import com.garden.smarttrim.mapper.AiAnalysisMapper;
import com.garden.smarttrim.mapper.TreeMapper;
import com.garden.smarttrim.service.AiService;
import com.garden.smarttrim.service.BranchAnalysisService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.AiAnalyzeVO;
import com.garden.smarttrim.vo.BranchLineVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 枝条识别服务实现
 *
 * <p>负责调用识别策略、落库 ai_analysis，并在 JSON 字符串与分支线条对象之间转换。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final BranchAnalysisService branchAnalysisService;
    private final AiAnalysisMapper aiAnalysisMapper;
    private final TreeMapper treeMapper;
    private final ObjectMapper objectMapper;

    @Override
    public AiAnalyzeVO analyze(AiAnalyzeDTO dto) {
        if (!SecurityUtil.isWorker()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // 解析树种：优先入参，其次由 treeId 反查
        String species = dto.getSpecies();
        if ((species == null || species.isBlank()) && dto.getTreeId() != null) {
            Tree tree = treeMapper.selectById(dto.getTreeId());
            if (tree != null) {
                species = tree.getSpecies();
            }
        }
        if (species == null || species.isBlank()) {
            species = "未知树种";
        }

        AiAnalyzeDTO req = new AiAnalyzeDTO();
        req.setImageUrl(dto.getImageUrl());
        req.setTreeId(dto.getTreeId());
        req.setTaskId(dto.getTaskId());
        req.setSpecies(species);
        req.setRemark(dto.getRemark());

        // 执行识别策略（mock / http）
        AiAnalyzeVO vo = branchAnalysisService.analyze(req);

        // 落库
        AiAnalysis entity = new AiAnalysis();
        entity.setTaskId(dto.getTaskId());
        entity.setTreeId(dto.getTreeId());
        entity.setSpecies(species);
        entity.setImageUrl(dto.getImageUrl());
        entity.setMarkedImageUrl(vo.getMarkedImageUrl());
        entity.setCutBranches(toJson(vo.getCutBranches()));
        entity.setKeepBranches(toJson(vo.getKeepBranches()));
        entity.setSuggestion(vo.getSuggestion());
        entity.setConfidence(vo.getConfidence() == null ? null : BigDecimal.valueOf(vo.getConfidence()));
        entity.setIsMock(Boolean.TRUE.equals(vo.getIsMock()) ? 1 : 0);
        entity.setModelVersion(vo.getModelVersion());
        aiAnalysisMapper.insert(entity);

        // 回填分析结果ID与创建时间
        vo.setAnalysisId(entity.getId());
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    @Override
    public AiAnalyzeVO result(Long taskId) {
        if (taskId == null) {
            return null;
        }
        AiAnalysis entity = aiAnalysisMapper.selectLatestByTaskId(taskId);
        if (entity == null) {
            return null;
        }
        return toVo(entity);
    }

    @Override
    public Map<String, Object> suggestion(Long taskId) {
        AiAnalyzeVO vo = result(taskId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("suggestion", vo == null ? "" : vo.getSuggestion());
        return map;
    }

    /** 实体转 VO（解析 JSON 线条数组） */
    private AiAnalyzeVO toVo(AiAnalysis entity) {
        AiAnalyzeVO vo = new AiAnalyzeVO();
        vo.setAnalysisId(entity.getId());
        vo.setTaskId(entity.getTaskId());
        vo.setTreeId(entity.getTreeId());
        vo.setImageUrl(entity.getImageUrl());
        vo.setMarkedImageUrl(entity.getMarkedImageUrl());
        vo.setConfidence(entity.getConfidence() == null ? null : entity.getConfidence().doubleValue());
        vo.setIsMock(entity.getIsMock() != null && entity.getIsMock() == 1);
        vo.setModelVersion(entity.getModelVersion());
        vo.setSuggestion(entity.getSuggestion());
        vo.setCutBranches(fromJson(entity.getCutBranches()));
        vo.setKeepBranches(fromJson(entity.getKeepBranches()));
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /** 线条列表转 JSON */
    private String toJson(List<BranchLineVO> lines) {
        try {
            return objectMapper.writeValueAsString(lines == null ? new ArrayList<>() : lines);
        } catch (Exception e) {
            log.error("枝条线条序列化失败", e);
            throw new BusinessException("AI 结果序列化失败");
        }
    }

    /** JSON 转线条列表 */
    private List<BranchLineVO> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<BranchLineVO>>() {
            });
        } catch (Exception e) {
            log.error("枝条线条反序列化失败", e);
            return new ArrayList<>();
        }
    }
}
