package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.service.FileService;
import com.garden.smarttrim.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件上传服务实现
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileUtil fileUtil;

    @Override
    public Map<String, Object> upload(MultipartFile file, String bizType) {
        String url = fileUtil.saveImage(file, bizType);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", file.getOriginalFilename());
        result.put("url", url);
        return result;
    }
}
