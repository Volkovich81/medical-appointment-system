package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Врач")
public class DoctorDTO extends PersonDTO {

    private List<Long> specializationIds;
}