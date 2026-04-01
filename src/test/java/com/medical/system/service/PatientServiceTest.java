package com.medical.system.service;

import com.medical.system.cache.PatientCache;
import com.medical.system.cache.PatientSearchKey;
import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Doctor;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Patient;
import com.medical.system.exception.BulkOperationException;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.DoctorRepository;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

// Развернутые статические импорты JUnit и Mockito для Checkstyle
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S1602", "java:S5960", "java:S5778"})
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private MedicalRecordRepository medicalRecordRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientCache patientCache;

    @InjectMocks private PatientService patientService;

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
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        PatientDTO result = patientService.getPatientById(1L);
        assertNotNull(result);
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
        verify(patientCache).clear();
    }

    @Test
    void updatePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // Используем thenAnswer для имитации сохранения и покрытия сеттеров
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientDTO updateData = new PatientDTO();
        updateData.setFirstName("Updated");
        updateData.setLastName("Updated");

        PatientDTO result = patientService.updatePatient(1L, updateData);
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
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
        verify(patientCache).clear();
    }

    @Test
    void saveAllWithoutTransaction_ThrowsBulkOperationException() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient));
        assertThrows(BulkOperationException.class, () -> patientService.saveAllWithoutTransaction(List.of(patientDto)));
        verify(patientCache).clear();
    }

    @Test
    void saveAllWithTransaction_ThrowsBulkOperationException() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient));
        assertThrows(BulkOperationException.class, () -> patientService.saveAllWithTransaction(List.of(patientDto)));
        verify(patientCache).clear();
    }

    @Test
    void createWithTransaction_NoError_Success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        PatientDTO result = patientService.createWithTransaction(patientDto, false);
        assertNotNull(result);
        verify(patientCache).clear();
    }

    @Test
    void createWithoutTransaction_NoError_Success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        PatientDTO result = patientService.createWithoutTransaction(patientDto, false);
        assertNotNull(result);
        verify(patientCache).clear();
    }

    @Test
    void getCacheSize_Success() {
        when(patientCache.size()).thenReturn(5);
        assertEquals(5, patientService.getCacheSize());
    }

    @Test
    void getPatientWithAppointments_Success() {
        when(patientRepository.findByIdWithAppointments(1L)).thenReturn(patient);
        assertNotNull(patientService.getPatientWithAppointments(1L));
    }

    @ParameterizedTest
    @CsvSource({
            "lastName, asc, true",
            "lastName, desc, true",
            "firstName, asc, true",
            "firstName, desc, true",
            "lastName, asc, false",
            "lastName, desc, false",
            "firstName, asc, false"
    })
    void findPatientsBySpecialization_CachingTests(String sortBy, String sortDir, boolean isJpql) {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientCache.containsKey(any(PatientSearchKey.class))).thenReturn(false);

        if (isJpql) {
            when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class))).thenReturn(page);
            patientService.findPatientsBySpecializationCached("Хирург", 0, 10, sortBy, sortDir);
        } else {
            when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class))).thenReturn(page);
            patientService.findPatientsBySpecializationNativeCached("Хирург", 0, 10, sortBy, sortDir);
        }
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_FromCache() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));
        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        patientService.findPatientsBySpecializationCached("Хирург", 0, 10, "lastName", "asc");
        verify(patientRepository, never()).findByDoctorSpecializationJpql(any(), any());
    }

    @Test
    void findPatientsBySpecializationNativeCached_FromCache() {
        Page<PatientDTO> cachedPage = new PageImpl<>(List.of(patientDto));
        when(patientCache.containsKey(any())).thenReturn(true);
        when(patientCache.get(any())).thenReturn(cachedPage);

        patientService.findPatientsBySpecializationNativeCached("Хирург", 0, 10, "lastName", "asc");
        verify(patientRepository, never()).findByDoctorSpecializationNative(any(), any());
    }

    @Test
    void getOrCreateDefaultDoctor_WhenDoctorNotFound() {
        Doctor newDoctor = new Doctor();
        newDoctor.setId(1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(newDoctor);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        patientService.createWithTransaction(patientDto, false);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void createWithTransaction_ThrowErrorTrue_ShouldNotCallInvalidateCache() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> patientService.createWithTransaction(patientDto, true));
        // Проверка сообщения важна для покрытия веток тернарного оператора в сервисе
        assertTrue(ex.getMessage().contains("откатится"));
        verify(patientCache, never()).clear();
    }

    @Test
    void createWithoutTransaction_ThrowErrorTrue_ShouldNotCallInvalidateCache() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> patientService.createWithoutTransaction(patientDto, true));
        // Проверка сообщения для покрытия второй ветки
        assertTrue(ex.getMessage().contains("уже сохранены"));
        verify(patientCache, never()).clear();
    }

    @Test
    void getPatientsByLastName_Coverage_NoResult() {
        when(patientRepository.findByLastNameIgnoreCase("Unknown")).thenReturn(List.of());
        List<PatientDTO> result = patientService.getPatientsByLastName("Unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    void createWithTransaction_Coverage_DifferentData() {
        Patient altPatient = new Patient();
        altPatient.setId(99L);
        when(patientRepository.save(any(Patient.class))).thenReturn(altPatient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(new Doctor()));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        PatientDTO result = patientService.createWithTransaction(patientDto, false);
        assertEquals(99L, result.getId());
    }

    @Test
    void getPatientsByLastName_EmptyString_Coverage() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        List<PatientDTO> result = patientService.getPatientsByLastName("");
        assertNotNull(result);
    }

    @Test
    void createWithoutTransaction_Coverage_DifferentData() {
        Patient altPatient = new Patient();
        altPatient.setId(88L);
        when(patientRepository.save(any(Patient.class))).thenReturn(altPatient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(new Doctor()));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        PatientDTO result = patientService.createWithoutTransaction(patientDto, false);
        assertEquals(88L, result.getId());
    }

    @Test
    void saveAll_EmptyList_Coverage() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of());
        List<PatientDTO> result = patientService.saveAll(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void saveAll_MultipleItems_Coverage() {
        when(patientRepository.saveAll(anyList())).thenReturn(List.of(patient, patient));
        List<PatientDTO> result = patientService.saveAll(List.of(patientDto, patientDto));
        assertEquals(2, result.size());
    }
}