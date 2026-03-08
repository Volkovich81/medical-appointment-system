package com.medical.system.controller;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
