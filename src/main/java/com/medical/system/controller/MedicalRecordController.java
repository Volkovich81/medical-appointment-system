package com.medical.system.controller;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @GetMapping
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/{id}")
    public MedicalRecordDTO getMedicalRecordById(@PathVariable Long id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @PostMapping
    public MedicalRecordDTO createMedicalRecord(@RequestBody MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordService.createMedicalRecord(medicalRecordDTO);
    }

    @PutMapping("/{id}")
    public MedicalRecordDTO updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordService.updateMedicalRecord(id, medicalRecordDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
    }
}