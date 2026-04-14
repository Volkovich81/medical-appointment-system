package com.medical.system.controller;

import com.medical.system.enums.TaskStatus;
import com.medical.system.service.AsyncOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Асинхронные операции", description = "Демонстрация @Async и CompletableFuture")
@RestController
@RequestMapping("/async")
@RequiredArgsConstructor
public class AsyncOperationController {

    private final AsyncOperationService asyncOperationService;

    @Operation(summary = "Запустить долгую асинхронную операцию")
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startAsyncOperation() {
        CompletableFuture<String> future = asyncOperationService.startLongOperation();
        Map<String, String> response = new HashMap<>();
        try {
            String taskId = future.get();
            response.put("taskId", taskId);
            response.put("message", "Операция запущена. Используйте /async/status/{taskId} для проверки статуса.");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            response.put("error", "Не удалось запустить операцию: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Проверить статус асинхронной операции по ID")
    @GetMapping("/status/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskStatus(@PathVariable String taskId) {
        TaskStatus status = asyncOperationService.getTaskStatus(taskId);
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        if (status == null) {
            response.put("status", "NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("status", status.name());
        return ResponseEntity.ok(response);
    }
}