package com.medical.system.service;

import com.medical.system.dto.PatientDTO;
import com.medical.system.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskExecutorService {

    private final PatientService patientService;

    @Async
    @SuppressWarnings("java:S2925")
    public void executeBulkImport(String taskId,
                                  List<PatientDTO> patients,
                                  Map<String, TaskStatus> statusMap,
                                  Map<String, Object> resultMap) {
        try {
            Thread.sleep(10000);
            statusMap.put(taskId, TaskStatus.PROCESSING);
            log.info("🟠 Задача {} в процессе, статус: PROCESSING", taskId);

            List<PatientDTO> savedPatients = patientService.saveAllWithTransaction(patients);

            Thread.sleep(10000);
            statusMap.put(taskId, TaskStatus.COMPLETED);
            resultMap.put(taskId, savedPatients);
            log.info("🟢 Задача {} завершена успешно, статус: COMPLETED, сохранено пациентов: {}",
                    taskId, savedPatients.size());

        } catch (IllegalArgumentException e) {
            statusMap.put(taskId, TaskStatus.FAILED);
            resultMap.put(taskId, e.getMessage());
            log.error("🔴 Задача {} завершилась с ошибкой валидации, статус: FAILED. Причина: {}",
                    taskId, e.getMessage());
        } catch (InterruptedException e) {
            statusMap.put(taskId, TaskStatus.FAILED);
            resultMap.put(taskId, "Операция прервана");
            log.error("🔴 Задача {} прервана, статус: FAILED", taskId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            statusMap.put(taskId, TaskStatus.FAILED);
            resultMap.put(taskId, "Внутренняя ошибка: " + e.getMessage());
            log.error("🔴 Задача {} завершилась с ошибкой, статус: FAILED", taskId, e);
        }
    }
}