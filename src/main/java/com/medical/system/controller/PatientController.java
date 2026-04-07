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

    @Operation(summary = "Найти пациента с записями (JOIN FETCH)")
    @GetMapping("/{id}/with-appointments")
    public ResponseEntity<PatientDTO> getPatientWithAppointments(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientWithAppointments(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать пациента")
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO created = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Создать пациента с транзакцией (демонстрация)")
    @PostMapping("/demo/with-transaction")
    public ResponseEntity<PatientDTO> createPatientWithTransaction(
            @Valid @RequestBody PatientDTO patientDTO,
            @RequestParam(defaultValue = "false") boolean throwError) {
        PatientDTO created = patientService.createWithTransaction(patientDTO, throwError);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Создать пациента без транзакции (демонстрация)")
    @PostMapping("/demo/without-transaction")
    public ResponseEntity<PatientDTO> createPatientWithoutTransaction(
            @Valid @RequestBody PatientDTO patientDTO,
            @RequestParam(defaultValue = "false") boolean throwError) {
        PatientDTO created = patientService.createWithoutTransaction(patientDTO, throwError);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Массовое создание пациентов")
    @PostMapping("/bulk")
    public ResponseEntity<List<PatientDTO>> createPatientsBulk(
            @RequestBody List<PatientDTO> patients) {
        List<PatientDTO> created = patientService.saveAll(patients);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Массовое создание пациентов без транзакции (демонстрация)")
    @PostMapping("/bulk/without-transaction")
    public ResponseEntity<List<PatientDTO>> createPatientsBulkWithoutTransaction(
            @RequestBody List<PatientDTO> patients) {
        List<PatientDTO> created = patientService.saveAllWithoutTransaction(patients);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Массовое создание пациентов с транзакцией (демонстрация)")
    @PostMapping("/bulk/with-transaction")
    public ResponseEntity<List<PatientDTO>> createPatientsBulkWithTransaction(
            @RequestBody List<PatientDTO> patients) {
        List<PatientDTO> created = patientService.saveAllWithTransaction(patients);
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

    @Operation(summary = "Поиск по специализации врача (JPQL с кэшем)")
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

    @Operation(summary = "Поиск по специализации врача (native query с кэшем)")
    @GetMapping("/search/by-specialization-native")
    public ResponseEntity<Page<PatientDTO>> findPatientsBySpecializationNative(
            @RequestParam String specializationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                specializationName, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить размер кэша")
    @GetMapping("/cache/status")
    public ResponseEntity<String> getCacheStatus() {
        return ResponseEntity.ok("Размер кэша: " + patientService.getCacheSize());
    }
}