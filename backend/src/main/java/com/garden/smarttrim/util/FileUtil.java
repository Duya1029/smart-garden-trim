package com.garden.smarttrim.util;

import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.config.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 *
 * <p>将上传图片按「业务类型 / 年月」分目录存储，文件名使用时间戳 + UUID，返回可直接访问的相对 URL。</p>
 * <p>存储路径示例： ./upload/trim/202610/20261018153012_a1b2c3d4.jpg</p>
 * <p>访问地址示例： /upload/trim/202610/20261018153012_a1b2c3d4.jpg</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUtil {

    private final FileProperties fileProperties;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 保存图片
     *
     * @param file    上传文件
     * @param bizType 业务类型目录（tree / trim / ai）
     * @return 可访问的相对 URL
     */
    public String saveImage(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        if (!fileProperties.getAllowExtList().contains(ext)) {
            throw new BusinessException("仅支持上传 " + fileProperties.getAllowExt() + " 格式的图片");
        }

        String type = (bizType == null || bizType.isBlank()) ? "common" : bizType.replaceAll("[^a-zA-Z]", "");
        String monthDir = LocalDate.now().format(MONTH_FORMATTER);
        String fileName = LocalDateTime.now().format(NAME_FORMATTER)
                + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "." + ext;

        // 相对目录： trim/202610
        String relativeDir = type + File.separator + monthDir;
        File dir = new File(fileProperties.getAbsoluteUploadPath(), relativeDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建上传目录失败：" + dir.getAbsolutePath());
        }

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }

        // 返回 /upload/trim/202610/xxx.jpg
        return fileProperties.getAccessPrefix() + "/" + type + "/" + monthDir + "/" + fileName;
    }

    /**
     * 删除图片
     *
     * @param url 相对 URL（/upload/...）
     * @return 是否删除成功
     */
    public boolean deleteImage(String url) {
        if (url == null || url.isBlank() || !url.startsWith(fileProperties.getAccessPrefix())) {
            return false;
        }
        String relative = url.substring(fileProperties.getAccessPrefix().length());
        File target = new File(fileProperties.getAbsoluteUploadPath(), relative);
        return target.exists() && target.delete();
    }
}
