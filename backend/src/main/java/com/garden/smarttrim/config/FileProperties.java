package com.garden.smarttrim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传配置项（对应 application.yml 中 garden.file）
 */
@Data
@Component
@ConfigurationProperties(prefix = "garden.file")
public class FileProperties {

    /** 物理存储目录 */
    private String uploadPath = "./upload";
    /** 对外访问前缀 */
    private String accessPrefix = "/upload";
    /** 允许的图片扩展名，逗号分隔 */
    private String allowExt = "jpg,jpeg,png,webp,bmp";

    /** 允许的扩展名列表 */
    public List<String> getAllowExtList() {
        if (allowExt == null || allowExt.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(allowExt.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /** 上传目录的绝对路径（供静态资源映射使用） */
    public String getAbsoluteUploadPath() {
        return new File(uploadPath).getAbsolutePath();
    }
}
