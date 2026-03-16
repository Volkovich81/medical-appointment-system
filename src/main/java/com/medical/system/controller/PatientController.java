package com.medical.system.controller;

import com.medical.system.dto.PatientDTO;
import com.medical.system.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getPatients(
            @RequestParam(required = false) String lastName) {
        List<PatientDTO> patients = patientService.getPatientsByLastName(lastName);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        PatientDTO created = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientDTO patientDTO) {
        PatientDTO updated = patientService.updatePatient(id, patientDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/with-appointments")
    public ResponseEntity<PatientDTO> getPatientWithAppointments(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientWithAppointments(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-specialization")
    public ResponseEntity<Page<PatientDTO>> findPatientsBySpecialization(
            @RequestParam String specializationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                specializationName, page, size, sortBy, sortDir
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/by-specialization-native")
    public ResponseEntity<Page<PatientDTO>> findPatientsBySpecializationNative(
            @RequestParam String specializationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                specializationName, page, size, sortBy, sortDir
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/cache/status")
    public ResponseEntity<String> getCacheStatus() {
        return ResponseEntity.ok("Размер кэша пациентов: " + patientService.getCacheSize());
    }

    @GetMapping("/nplus1-problem")
    public ResponseEntity<List<PatientDTO>> testNPlus1Problem() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @PostMapping("/test-no-tx")
    public PatientDTO testNoTx(@RequestBody PatientDTO patientDTO, @RequestParam boolean error) {
        return patientService.createWithoutTransaction(patientDTO, error);
    }

    @PostMapping("/test-tx")
    public PatientDTO testTx(@RequestBody PatientDTO patientDTO, @RequestParam boolean error) {
        return patientService.createWithTransaction(patientDTO, error);
    }
}