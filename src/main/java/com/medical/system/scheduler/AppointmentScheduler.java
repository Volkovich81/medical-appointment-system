package com.medical.system.scheduler;

import com.medical.system.entity.Appointment;
import com.medical.system.enums.AppointmentStatus;
import com.medical.system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000) // раз в минуту
    @Transactional
    public void completePastAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> past = appointmentRepository
                .findByStatusAndAppointmentDateBefore(AppointmentStatus.SCHEDULED, now);

        if (!past.isEmpty()) {
            past.forEach(a -> a.setStatus(AppointmentStatus.COMPLETED));
            appointmentRepository.saveAll(past);
            log.info("✅ Завершено записей: {}", past.size());
        }
    }
}