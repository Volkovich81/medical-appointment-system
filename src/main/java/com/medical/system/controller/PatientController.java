package com.medical.system.controller;

import com.medical.system.dto.PatientDTO;
import com.medical.system.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Пациенты", description = "Управление пациентами")
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Получить всех пациентов")
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getPatients(
            @RequestParam(required = false) String lastName) {
        return ResponseEntity.ok(patientService.getPatientsByLastName(lastName));
    }

    @Operation(summary = "Найти пациента по ID")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать пациента")
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO created = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Изменить пациента")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO updated = patientService.updatePatient(id, patientDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить пациента")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск по специализации врача")
    @GetMapping("/search/by-specialization")
    public ResponseEntity<Page<PatientDTO>> findPatientsBySpecialization(
            @RequestParam String specializationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                specializationName, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cache/status")
    public ResponseEntity<String> getCacheStatus() {
        return ResponseEntity.ok("Размер кэша: " + patientService.getCacheSize());
    }
}