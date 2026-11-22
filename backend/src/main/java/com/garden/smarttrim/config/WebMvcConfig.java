package com.garden.smarttrim.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置
 *
 * <p>1) 注册 JWT 登录拦截器，放行登录接口与上传文件静态访问；
 * 2) 将本地上传目录映射为可访问的静态资源路径。</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final FileProperties fileProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",      // 登录
                        "/auth/logout",     // 退出
                        "/error",
                        "/upload/**"        // 上传的图片直接静态访问
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 garden.file.upload-path 目录映射到 garden.file.access-prefix 路径
        registry.addResourceHandler(fileProperties.getAccessPrefix() + "/**")
                .addResourceLocations("file:" + fileProperties.getAbsoluteUploadPath() + "/");
    }
}
