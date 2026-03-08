package com.medical.system.dto;

import lombok.Data;
import java.util.List;

@Data
public class DoctorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private List<Long> specializationIds;
}
