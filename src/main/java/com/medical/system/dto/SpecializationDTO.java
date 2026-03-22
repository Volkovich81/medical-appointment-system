package com.medical.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Специализация врача")
public class SpecializationDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Название специализации", example = "Хирург")
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String name;

    @Schema(description = "Описание", example = "Занимается хирургическими операциями")
    private String description;
}