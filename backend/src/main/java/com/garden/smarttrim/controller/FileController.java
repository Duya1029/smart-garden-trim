package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传模块 /files（登录用户）
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /** 上传图片，返回 {name, url} */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                              @RequestParam(required = false) String bizType) {
        return Result.ok(fileService.upload(file, bizType));
    }
}
