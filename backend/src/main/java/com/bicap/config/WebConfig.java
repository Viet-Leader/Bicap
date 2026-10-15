package com.bicap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resolveUploadDirectory().toUri().toString());

    }

    private Path resolveUploadDirectory() {
        Path configuredPath = Paths.get("uploads");
        if (Files.exists(configuredPath)) {
            return configuredPath.toAbsolutePath().normalize();
        }

        Path workspacePath = Paths.get("backend", "uploads");
        if (Files.exists(workspacePath)) {
            return workspacePath.toAbsolutePath().normalize();
        }

        return configuredPath.toAbsolutePath().normalize();
    }

}
