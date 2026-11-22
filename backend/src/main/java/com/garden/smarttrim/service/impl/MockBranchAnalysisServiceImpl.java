package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.service.BranchAnalysisService;
import com.garden.smarttrim.vo.AiAnalyzeVO;
import com.garden.smarttrim.vo.BranchLineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Mock 枝条识别策略实现
 *
 * <p>provider = mock（缺省）时生效。结果为确定性可复现：以 species + imageUrl 作为随机种子，
 * 相同输入始终产出相同的枝条坐标与置信度，便于演示与联调。</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "garden.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockBranchAnalysisServiceImpl implements BranchAnalysisService {

    /** 建议剪除枝条标签候选 */
    private static final String[] CUT_LABELS = {"下垂枝", "交叉枝", "病虫枝", "内膛枝", "徒长枝"};
    /** 建议保留枝条标签候选 */
    private static final String[] KEEP_LABELS = {"主干", "主枝延长枝", "结果母枝", "健康侧枝"};
    /** Mock 模型版本标识 */
    private static final String MOCK_VERSION = "mock-v1.0";

    @Override
    public AiAnalyzeVO analyze(AiAnalyzeDTO dto) {
        String species = (dto.getSpecies() == null || dto.getSpecies().isBlank())
                ? "未知树种" : dto.getSpecies();
        // 以 species + imageUrl 为种子，保证确定性可复现
        String imageUrl = dto.getImageUrl() == null ? "" : dto.getImageUrl();
        long seed = (species + "|" + imageUrl).hashCode();
        Random random = new Random(seed);

        int cutCount = 2 + random.nextInt(3);   // 2~4 条
        int keepCount = 2 + random.nextInt(2);  // 2~3 条

        List<BranchLineVO> cutBranches = new ArrayList<>();
        for (int i = 0; i < cutCount; i++) {
            cutBranches.add(randomLine(random, CUT_LABELS[random.nextInt(CUT_LABELS.length)]));
        }
        List<BranchLineVO> keepBranches = new ArrayList<>();
        for (int i = 0; i < keepCount; i++) {
            keepBranches.add(randomLine(random, KEEP_LABELS[random.nextInt(KEEP_LABELS.length)]));
        }

        double confidence = round2(0.78 + random.nextDouble() * 0.17); // 0.78~0.95

        AiAnalyzeVO vo = new AiAnalyzeVO();
        vo.setTaskId(dto.getTaskId());
        vo.setTreeId(dto.getTreeId());
        vo.setImageUrl(dto.getImageUrl());
        vo.setMarkedImageUrl(null);
        vo.setConfidence(confidence);
        vo.setIsMock(true);
        vo.setModelVersion(MOCK_VERSION);
        vo.setSuggestion(buildSuggestion(species, cutBranches, keepBranches));
        vo.setCutBranches(cutBranches);
        vo.setKeepBranches(keepBranches);
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    /** 生成一条归一化坐标（0~1）的枝条线条 */
    private BranchLineVO randomLine(Random random, String label) {
        BranchLineVO line = new BranchLineVO();
        line.setX1(round2(0.10 + random.nextDouble() * 0.80));
        line.setY1(round2(0.10 + random.nextDouble() * 0.80));
        line.setX2(round2(0.10 + random.nextDouble() * 0.80));
        line.setY2(round2(0.10 + random.nextDouble() * 0.80));
        line.setLabel(label);
        return line;
    }

    /** 统一模板 + 树种拼接，包含剪除项 / 保留项 / 剪口处理 / 造型建议四段 */
    private String buildSuggestion(String species, List<BranchLineVO> cutBranches, List<BranchLineVO> keepBranches) {
        String cutText = cutBranches.stream().map(BranchLineVO::getLabel).distinct()
                .collect(Collectors.joining("、"));
        String keepText = keepBranches.stream().map(BranchLineVO::getLabel).distinct()
                .collect(Collectors.joining("、"));
        return String.format("该%s树冠内膛枝过密，建议："
                + "1) 剪除项：剪除%s，避免遮挡路灯与行人通行；"
                + "2) 保留项：保留%s，维持树体骨架与结果能力；"
                + "3) 剪口处理：剪口距枝条基部 1-2cm，呈 45° 斜面，并涂抹愈合剂；"
                + "4) 造型建议：维持自然开心形，疏除交叉重叠枝条，改善通风透光。",
                species, cutText, keepText);
    }

    /** 保留两位小数 */
    private double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
