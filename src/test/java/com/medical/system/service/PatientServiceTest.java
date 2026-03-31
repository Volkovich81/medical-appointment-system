package com.medical.system.service;

import com.medical.system.cache.PatientCache;
import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Doctor;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Patient;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.DoctorRepository;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientCache patientCache;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDto;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Иван");
        patient.setLastName("Иванов");
        patient.setPhone("+375291234567");
        patient.setEmail("ivan@example.com");

        patientDto = new PatientDTO();
        patientDto.setId(1L);
        patientDto.setFirstName("Иван");
        patientDto.setLastName("Иванов");
        patientDto.setPhone("+375291234567");
        patientDto.setEmail("ivan@example.com");
    }

    @Test
    void getAllPatients_Success() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getAllPatients();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getFirstName());
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
    }

    @Test
    void getPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        PatientDTO result = patientService.getPatientById(1L);

        assertNull(result);
    }

    @Test
    void getPatientsByLastName_Success() {
        when(patientRepository.findByLastNameIgnoreCase("Иванов")).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getPatientsByLastName("Иванов");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getPatientsByLastName_Empty_ReturnsAll() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getPatientsByLastName(null);

        assertFalse(result.isEmpty());
        verify(patientRepository, never()).findByLastNameIgnoreCase(any());
    }

    @Test
    void createPatient_Success() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.createPatient(patientDto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        verify(patientCache).clear();
    }

    @Test
    void updatePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.updatePatient(1L, patientDto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        verify(patientCache).clear();
    }

    @Test
    void updatePatient_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        PatientDTO result = patientService.updatePatient(1L, patientDto);

        assertNull(result);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void deletePatient_Success() {
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
        verify(patientCache).clear();
    }

    @Test
    void saveAll_Success() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.saveAll(List.of(patientDto));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(patientRepository).saveAll(anyList());
        verify(patientCache).clear();
    }

    @Test
    void saveAllWithoutTransaction_ThrowsException() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient));

        assertThrows(RuntimeException.class, () ->
                patientService.saveAllWithoutTransaction(List.of(patientDto))
        );

        verify(patientRepository).saveAll(anyList());
        verify(patientCache).clear();
    }

    @Test
    void saveAllWithTransaction_ThrowsException() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient));

        assertThrows(RuntimeException.class, () ->
                patientService.saveAllWithTransaction(List.of(patientDto))
        );

        verify(patientRepository).saveAll(anyList());
        verify(patientCache).clear();
    }

    @Test
    void createWithTransaction_WithError_ThrowsException() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertThrows(IllegalStateException.class, () ->
                patientService.createWithTransaction(patientDto, true)
        );
    }

    @Test
    void createWithoutTransaction_WithError_ThrowsException() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertThrows(IllegalStateException.class, () ->
                patientService.createWithoutTransaction(patientDto, true)
        );
    }

    @Test
    void getCacheSize_Success() {
        when(patientCache.size()).thenReturn(5);

        int result = patientService.getCacheSize();

        assertEquals(5, result);
    }

    @Test
    void getPatientWithAppointments_Success() {
        when(patientRepository.findByIdWithAppointments(1L)).thenReturn(patient);

        PatientDTO result = patientService.getPatientWithAppointments(1L);

        assertNotNull(result);
        verify(patientRepository).findByIdWithAppointments(1L);
    }

    @Test
    void findPatientsBySpecializationNativeCached_FromDatabase() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_FromCache() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));

        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientRepository, never()).findByDoctorSpecializationNative(any(), any());
    }

    @Test
    void createWithTransaction_NoError_Success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        Appointment appointment = new Appointment();

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithTransaction(patientDto, false);

        assertNotNull(result);
        verify(patientCache).clear();
    }

    @Test
    void createWithoutTransaction_NoError_Success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        Appointment appointment = new Appointment();

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithoutTransaction(patientDto, false);

        assertNotNull(result);
        verify(patientCache).clear();
    }

    @Test
    void createWithTransaction_WithDefaultDoctor_Success() {
        Doctor defaultDoctor = new Doctor();
        defaultDoctor.setId(1L);
        defaultDoctor.setFirstName("Доктор");
        defaultDoctor.setLastName("По умолчанию");
        Appointment appointment = new Appointment();

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(defaultDoctor);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithTransaction(patientDto, false);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
        verify(appointmentRepository).save(any(Appointment.class));
        verify(patientCache).clear();
    }

    @Test
    void createWithoutTransaction_WithExistingDoctor_Success() {
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(1L);
        existingDoctor.setFirstName("Доктор");
        existingDoctor.setLastName("Существующий");
        Appointment appointment = new Appointment();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithoutTransaction(patientDto, false);

        assertNotNull(result);
        verify(doctorRepository, never()).save(any());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(patientCache).clear();
    }

    @Test
    void findPatientsBySpecializationCached_FromDatabase() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_FromCache() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));

        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientRepository, never()).findByDoctorSpecializationJpql(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithDescSort() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "desc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void getPatientById_Coverage() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        verify(patientRepository).findById(1L);
    }

    @Test
    void getAllPatients_Coverage() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getAllPatients();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getPatientsByLastName_WithLastName_Coverage() {
        when(patientRepository.findByLastNameIgnoreCase("Иванов")).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getPatientsByLastName("Иванов");

        assertFalse(result.isEmpty());
        verify(patientRepository).findByLastNameIgnoreCase("Иванов");
    }

    @Test
    void getPatientsByLastName_EmptyString_ReturnsAll() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getPatientsByLastName("");

        assertFalse(result.isEmpty());
        verify(patientRepository, never()).findByLastNameIgnoreCase(any());
    }

    @Test
    void updatePatient_LambdaCoverage() {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("Обновлен");
        updatedPatient.setLastName("Обновленов");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        PatientDTO result = patientService.updatePatient(1L, patientDto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        verify(patientCache).clear();
    }

    @Test
    void getOrCreateDefaultDoctor_WhenDoctorExists_Coverage() {
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(1L);
        existingDoctor.setFirstName("Доктор");
        existingDoctor.setLastName("Существующий");
        Appointment appointment = new Appointment();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithoutTransaction(patientDto, false);

        assertNotNull(result);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void getOrCreateDefaultDoctor_WhenDoctorNotFound_Coverage() {
        Doctor newDoctor = new Doctor();
        newDoctor.setId(1L);
        newDoctor.setFirstName("Доктор");
        newDoctor.setLastName("По умолчанию");
        Appointment appointment = new Appointment();

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(newDoctor);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        PatientDTO result = patientService.createWithTransaction(patientDto, false);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void invalidateCache_Coverage() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.createPatient(patientDto);

        verify(patientCache).clear();
    }

    @Test
    void createPatient_Coverage() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.createPatient(patientDto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        verify(patientCache).clear();
    }

    @Test
    void deletePatient_Coverage() {
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
        verify(patientCache).clear();
    }

    @Test
    void findPatientsBySpecializationCached_WithSortByLastName() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithSortByFirstName() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "firstName", "desc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithSortByLastName() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithSortByFirstName() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "firstName", "desc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithAscSort_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithDescSort_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "desc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithCacheHit() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));

        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientRepository, never()).findByDoctorSpecializationJpql(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithAscSort_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithDescSort_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "desc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithCacheHit() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));

        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached(
                "Хирург", 0, 10, "lastName", "asc");

        assertNotNull(result);
        verify(patientRepository, never()).findByDoctorSpecializationNative(any(), any());
    }

    @Test
    void updatePatient_WithDifferentPrice_NotNeeded() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.updatePatient(1L, patientDto);

        assertNotNull(result);
        verify(patientCache).clear();
    }

    @Test
    void findPatientsBySpecializationCached_AscSort() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        patientService.findPatientsBySpecializationCached("Хирург", 0, 10, "lastName", "asc");

        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_DescSort() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        patientService.findPatientsBySpecializationCached("Хирург", 0, 10, "lastName", "desc");

        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_AscSort() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        patientService.findPatientsBySpecializationNativeCached("Хирург", 0, 10, "lastName", "asc");

        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_DescSort() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class)))
                .thenReturn(page);

        patientService.findPatientsBySpecializationNativeCached("Хирург", 0, 10, "lastName", "desc");

        verify(patientCache).put(any(), any());
    }
}