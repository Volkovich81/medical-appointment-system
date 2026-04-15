package com.medical.system.service;

import com.medical.system.dto.PatientDTO;
import com.medical.system.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncOperationService {

    private final Map<String, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Object> taskResultMap = new ConcurrentHashMap<>();
    private final AsyncTaskExecutorService asyncTaskExecutorService;

    public String startBulkPatientImport(List<PatientDTO> patients) {
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, TaskStatus.PENDING);
        log.info("🟡 Задача {} (массовый импорт пациентов) создана, статус: PENDING", taskId);
        asyncTaskExecutorService.executeBulkImport(taskId, patients, taskStatusMap, taskResultMap);
        return taskId;
    }

    public TaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.get(taskId);
    }

    public Object getTaskResult(String taskId) {
        return taskResultMap.get(taskId);
    }
}