package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Пациент")
public class PatientDTO extends PersonDTO {

    @Schema(description = "Дата рождения", example = "1990-01-01")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    private List<AppointmentDTO> appointments;
    private MedicalRecordDTO medicalRecord;
}