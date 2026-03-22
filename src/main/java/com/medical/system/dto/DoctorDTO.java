package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Врач")
public class DoctorDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Имя", example = "Анна")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @Schema(description = "Фамилия", example = "Петрова")
    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @Schema(description = "Телефон", example = "+375447973155")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Неверный формат телефона")
    private String phone;

    @Schema(description = "Email", example = "doctor@clinic.com")
    @Email(message = "Неверный формат email")
    private String email;

    private List<Long> specializationIds;
}