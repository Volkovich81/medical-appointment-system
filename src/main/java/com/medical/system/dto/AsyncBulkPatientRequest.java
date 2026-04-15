package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class AsyncBulkPatientRequest {
    @Schema(description = "Список пациентов для массового добавления")
    private List<PatientDTO> patients;
}