package com.medical.system.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicalRecordDTO {
    private Long id;
    private LocalDate recordDate;
    private String diagnosis;
    private String treatment;
    private Long patientId;
    private String patientName;
}
