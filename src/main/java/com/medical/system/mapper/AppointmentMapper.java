package com.medical.system.mapper;

import com.medical.system.dto.AppointmentDTO;
import com.medical.system.entity.Appointment;

public class AppointmentMapper {
    private AppointmentMapper() {}

    public static AppointmentDTO toDto(Appointment appointment) {
        if (appointment == null) return null;

        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());

        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatientName(appointment.getPatient().getFirstName() + " " +
                    appointment.getPatient().getLastName());
        }

        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());
            dto.setDoctorName(appointment.getDoctor().getFirstName() + " " +
                    appointment.getDoctor().getLastName());
        }

        return dto;
    }
}
