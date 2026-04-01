package com.medical.system.service;

import com.medical.system.cache.PatientCache;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S1602", "java:S5960"})
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
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.updatePatient(1L, patientDto);

        assertNotNull(result);
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
    void findPatientsBySpecializationCached_FromDatabase() {
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

    @ParameterizedTest
    @CsvSource({
            "lastName, asc",
            "lastName, desc",
            "firstName, asc",
            "firstName, desc"
    })
    void findPatientsBySpecializationCached_VariousSorts(String sortBy, String sortDir) {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached(
                "Хирург", 0, 10, sortBy, sortDir);

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
                "Хирург", 0, 10, "firstName", "asc");

        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void getOrCreateDefaultDoctor_WhenDoctorExists() {
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(1L);
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
    void getOrCreateDefaultDoctor_WhenDoctorNotFound() {
        Doctor newDoctor = new Doctor();
        newDoctor.setId(1L);
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

    @SuppressWarnings("java:S1602")
    @Test
    void createWithTransaction_ThrowErrorTrue_ShouldNotCallInvalidateCache() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertThrows(IllegalStateException.class, () -> patientService.createWithTransaction(patientDto, true));

        verify(patientCache, never()).clear();
    }

    @SuppressWarnings("java:S1602")
    @Test
    void createWithoutTransaction_ThrowErrorTrue_ShouldNotCallInvalidateCache() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(new MedicalRecord());
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertThrows(IllegalStateException.class, () -> patientService.createWithoutTransaction(patientDto, true));

        verify(patientCache, never()).clear();
    }

    @Test
    void createWithTransaction_ThrowErrorFalse_Coverage() {
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
    void createWithoutTransaction_ThrowErrorFalse_Coverage() {
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
    void getPatientsByLastName_WithLastName_Coverage() {
        when(patientRepository.findByLastNameIgnoreCase("Иванов")).thenReturn(List.of(patient));
        List<PatientDTO> result = patientService.getPatientsByLastName("Иванов");
        assertFalse(result.isEmpty());
    }

    @Test
    void findPatientsBySpecializationNativeCached_WithDescSort_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationNative(any(), any(Pageable.class))).thenReturn(page);
        Page<PatientDTO> result = patientService.findPatientsBySpecializationNativeCached("Хирург", 0, 10, "lastName", "desc");
        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }

    @Test
    void findPatientsBySpecializationCached_WithFirstNameDesc_Coverage() {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientCache.containsKey(any())).thenReturn(false);
        when(patientRepository.findByDoctorSpecializationJpql(any(), any(Pageable.class))).thenReturn(page);
        Page<PatientDTO> result = patientService.findPatientsBySpecializationCached("Хирург", 0, 10, "firstName", "desc");
        assertNotNull(result);
        verify(patientCache).put(any(), any());
    }
}