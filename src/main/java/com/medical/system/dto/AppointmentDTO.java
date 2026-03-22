package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Запись на приём")
public class AppointmentDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Дата и время приёма", example = "2026-05-20T14:30:00")
    @NotNull(message = "Дата приёма обязательна")
    @Future(message = "Дата приёма должна быть в будущем")
    private LocalDateTime appointmentDate;

    @Schema(description = "Статус", example = "SCHEDULED")
    @NotBlank(message = "Статус не может быть пустым")
    @Pattern(regexp = "SCHEDULED|COMPLETED|CANCELLED",
            message = "Статус должен быть SCHEDULED, COMPLETED или CANCELLED")
    private String status;

    @Schema(description = "ID пациента", example = "1")
    @NotNull(message = "ID пациента обязателен")
    @Positive(message = "ID пациента должен быть положительным")
    private Long patientId;

    @Schema(description = "ID врача", example = "1")
    @NotNull(message = "ID врача обязателен")
    @Positive(message = "ID врача должен быть положительным")
    private Long doctorId;

    private String patientName;
    private String doctorName;
}