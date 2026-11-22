package com.garden.smarttrim.service;

import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.vo.AiAnalyzeVO;

/**
 * 枝条识别策略接口
 *
 * <p>mock 实现返回确定性模拟数据；http 实现转发到真实模型服务。
 * 由 AiService 负责落库，策略仅负责产出识别结果。</p>
 */
public interface BranchAnalysisService {

    /** 执行枝条识别分析 */
    AiAnalyzeVO analyze(AiAnalyzeDTO dto);
}
