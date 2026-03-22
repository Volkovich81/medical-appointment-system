package com.medical.system.controller;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.service.MedicalRecordService;
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

@Tag(name = "Медицинские карты", description = "Управление медицинскими картами пациентов")
@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @Operation(summary = "Получить все медицинские карты")
    @GetMapping
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @Operation(summary = "Найти медицинскую карту по ID")
    @GetMapping("/{id}")
    public MedicalRecordDTO getMedicalRecordById(@PathVariable Long id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @Operation(summary = "Создать медицинскую карту")
    @PostMapping
    public MedicalRecordDTO createMedicalRecord(@Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordService.createMedicalRecord(medicalRecordDTO);
    }

    @Operation(summary = "Изменить медицинскую карту")
    @PutMapping("/{id}")
    public MedicalRecordDTO updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordService.updateMedicalRecord(id, medicalRecordDTO);
    }

    @Operation(summary = "Удалить медицинскую карту")
    @DeleteMapping("/{id}")
    public void deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
    }
}