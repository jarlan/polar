package com.polarsirkelrock.dancechallenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.qr-output-dir:./generated-qr}")
    private String qrOutputDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(qrOutputDir).toAbsolutePath().toString();
        registry.addResourceHandler("/qr/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
