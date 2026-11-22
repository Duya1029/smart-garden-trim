package com.garden.smarttrim.service;

import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.vo.AiAnalyzeVO;

import java.util.Map;

/**
 * AI 枝条识别服务
 */
public interface AiService {

    /** 提交识别并落库，返回识别结果 */
    AiAnalyzeVO analyze(AiAnalyzeDTO dto);

    /** 查询某任务最近一次分析结果（无则 null） */
    AiAnalyzeVO result(Long taskId);

    /** 获取 AI 建议文本，返回 {suggestion} */
    Map<String, Object> suggestion(Long taskId);
}
