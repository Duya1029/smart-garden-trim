package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.AiAnalyzeDTO;
import com.garden.smarttrim.service.AiService;
import com.garden.smarttrim.vo.AiAnalyzeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AI 识别模块 /ai
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /** 提交识别（养护人员） */
    @PostMapping("/analyze")
    public Result<AiAnalyzeVO> analyze(@Valid @RequestBody AiAnalyzeDTO dto) {
        return Result.ok(aiService.analyze(dto));
    }

    /** 分析结果（登录用户） */
    @GetMapping("/result")
    public Result<AiAnalyzeVO> result(@RequestParam(required = false) Long taskId) {
        return Result.ok(aiService.result(taskId));
    }

    /** 建议文本（登录用户） */
    @GetMapping("/suggestion")
    public Result<Map<String, Object>> suggestion(@RequestParam(required = false) Long taskId) {
        return Result.ok(aiService.suggestion(taskId));
    }
}
