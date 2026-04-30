package com.medical.system.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Resource> handleError(HttpServletRequest request) {
        // Игнорируем ошибки API (чтобы они возвращали JSON ответ)
        String path = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        if (path != null && path.startsWith("/api")) {
            return ResponseEntity.notFound().build();
        }
        // Для всех остальных ошибок (404) отдаём index.html
        Resource resource = new ClassPathResource("/META-INF/resources/index.html");
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
}