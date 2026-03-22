package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Медицинская карта пациента")
public class MedicalRecordDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Дата записи", example = "2026-03-22")
    @NotNull(message = "Дата записи обязательна")
    @PastOrPresent(message = "Дата записи не может быть в будущем")
    private LocalDate recordDate;

    @Schema(description = "Диагноз", example = "Грипп")
    private String diagnosis;

    @Schema(description = "Лечение", example = "Постельный режим, обильное питьё")
    private String treatment;

    @Schema(description = "ID пациента", example = "1")
    @NotNull(message = "ID пациента обязателен")
    @Positive(message = "ID пациента должен быть положительным")
    private Long patientId;

    private String patientName;
}