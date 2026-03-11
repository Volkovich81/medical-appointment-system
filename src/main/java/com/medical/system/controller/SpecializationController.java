package com.medical.system.controller;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.service.SpecializationService;
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
@RequestMapping("/specializations")
@RequiredArgsConstructor
public class SpecializationController {
    private final SpecializationService specializationService;

    @GetMapping
    public List<SpecializationDTO> getAllSpecializations() {
        return specializationService.getAllSpecializations();
    }

    @GetMapping("/{id}")
    public SpecializationDTO getSpecializationById(@PathVariable Long id) {
        return specializationService.getSpecializationById(id);
    }

    @PostMapping
    public SpecializationDTO createSpecialization(@RequestBody SpecializationDTO specializationDTO) {
        return specializationService.createSpecialization(specializationDTO);
    }

    @PutMapping("/{id}")
    public SpecializationDTO updateSpecialization(
            @PathVariable Long id,
            @RequestBody SpecializationDTO specializationDTO) {
        return specializationService.updateSpecialization(
                id,
                specializationDTO
        );
    }

    @DeleteMapping("/{id}")
    public void deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
    }
}