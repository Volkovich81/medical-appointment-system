package com.medical.system.service;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Patient;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;
    private MedicalRecordDTO medicalRecordDto;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Иван");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setRecordDate(LocalDate.now());
        medicalRecord.setDiagnosis("Грипп");
        medicalRecord.setTreatment("Постельный режим");
        medicalRecord.setPatient(patient);

        medicalRecordDto = new MedicalRecordDTO();
        medicalRecordDto.setId(1L);
        medicalRecordDto.setRecordDate(LocalDate.now());
        medicalRecordDto.setDiagnosis("Грипп");
        medicalRecordDto.setTreatment("Постельный режим");
        medicalRecordDto.setPatientId(1L);
    }

    @Test
    void getAllMedicalRecords_Success() {
        when(medicalRecordRepository.findAll()).thenReturn(List.of(medicalRecord));

        List<MedicalRecordDTO> result = medicalRecordService.getAllMedicalRecords();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getMedicalRecordById_Success() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(medicalRecord));

        MedicalRecordDTO result = medicalRecordService.getMedicalRecordById(1L);

        assertNotNull(result);
        assertEquals("Грипп", result.getDiagnosis());
    }

    @Test
    void getMedicalRecordById_NotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        MedicalRecordDTO result = medicalRecordService.getMedicalRecordById(1L);

        assertNull(result);
    }

    @Test
    void createMedicalRecord_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecordDTO result = medicalRecordService.createMedicalRecord(medicalRecordDto);

        assertNotNull(result);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                medicalRecordService.createMedicalRecord(medicalRecordDto)
        );
    }

    @Test
    void updateMedicalRecord_Success() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(1L, medicalRecordDto);

        assertNotNull(result);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void updateMedicalRecord_NotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(1L, medicalRecordDto);

        assertNull(result);
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void updateMedicalRecord_NotFound_ReturnsNull() {
        when(medicalRecordRepository.findById(999L)).thenReturn(Optional.empty());

        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(999L, medicalRecordDto);

        assertNull(result);
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void deleteMedicalRecord_Success() {
        doNothing().when(medicalRecordRepository).deleteById(1L);

        medicalRecordService.deleteMedicalRecord(1L);

        verify(medicalRecordRepository).deleteById(1L);
    }
}