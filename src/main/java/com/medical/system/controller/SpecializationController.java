package com.medical.system.controller;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Специализации", description = "Управление специализациями врачей")
@RestController
@RequestMapping("/specializations")
@RequiredArgsConstructor
public class SpecializationController {
    private final SpecializationService specializationService;

    @Operation(summary = "Получить все специализации")
    @GetMapping
    public List<SpecializationDTO> getAllSpecializations() {
        return specializationService.getAllSpecializations();
    }

    @Operation(summary = "Найти специализацию по ID")
    @GetMapping("/{id}")
    public SpecializationDTO getSpecializationById(@PathVariable Long id) {
        return specializationService.getSpecializationById(id);
    }

    @Operation(summary = "Создать специализацию")
    @PostMapping
    public SpecializationDTO createSpecialization(@Valid @RequestBody SpecializationDTO specializationDTO) {
        return specializationService.createSpecialization(specializationDTO);
    }

    @Operation(summary = "Изменить специализацию")
    @PutMapping("/{id}")
    public SpecializationDTO updateSpecialization(
            @PathVariable Long id,
            @Valid @RequestBody SpecializationDTO specializationDTO) {
        return specializationService.updateSpecialization(id, specializationDTO);
    }

    @Operation(summary = "Удалить специализацию")
    @DeleteMapping("/{id}")
    public void deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
    }
}