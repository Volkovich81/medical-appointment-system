package com.medical.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // Если путь пустой или заканчивается на "/", отдаём index.html
                        if (resourcePath.isEmpty() || resourcePath.endsWith("/")) {
                            Resource index = new ClassPathResource("/META-INF/resources/index.html");
                            if (index.exists()) {
                                return index;
                            }
                        }
                        // Пытаемся найти файл по указанному пути
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // Если ничего не нашли – отдаём index.html (SPA fallback)
                        return new ClassPathResource("/META-INF/resources/index.html");
                    }
                });
    }
}