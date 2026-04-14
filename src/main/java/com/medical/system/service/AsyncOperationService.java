package com.medical.system.service;

import com.medical.system.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AsyncOperationService {

    private final Map<String, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Async
    public CompletableFuture<String> startLongOperation() {
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, TaskStatus.PENDING);
        log.info("🟡 Задача {} создана, статус: PENDING", taskId);

        try {
            Thread.sleep(2000);
            taskStatusMap.put(taskId, TaskStatus.PROCESSING);
            log.info("🟠 Задача {} в процессе, статус: PROCESSING", taskId);

            Thread.sleep(3000);
            taskStatusMap.put(taskId, TaskStatus.COMPLETED);
            log.info("🟢 Задача {} завершена, статус: COMPLETED", taskId);

        } catch (InterruptedException e) {
            taskStatusMap.put(taskId, TaskStatus.FAILED);
            log.error("🔴 Задача {} прервана, статус: FAILED", taskId, e);
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture(taskId);
    }

    public TaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.get(taskId);
    }
}