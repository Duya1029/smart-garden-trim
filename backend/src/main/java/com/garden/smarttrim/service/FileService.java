package com.garden.smarttrim.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传服务
 */
public interface FileService {

    /** 上传图片，返回 {name, url} */
    Map<String, Object> upload(MultipartFile file, String bizType);
}
