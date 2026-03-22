package com.medical.system.controller;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Врачи", description = "Управление врачами")
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Получить всех врачей")
    @GetMapping
    public List<DoctorDTO> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @Operation(summary = "Найти врача по ID")
    @GetMapping("/{id}")
    public DoctorDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @Operation(summary = "Создать врача")
    @PostMapping
    public DoctorDTO createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        return doctorService.createDoctor(doctorDTO);
    }

    @Operation(summary = "Изменить врача")
    @PutMapping("/{id}")
    public DoctorDTO updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDTO doctorDTO) {
        return doctorService.updateDoctor(id, doctorDTO);
    }

    @Operation(summary = "Удалить врача")
    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }
}