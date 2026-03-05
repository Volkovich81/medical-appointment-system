package com.medical.system.controller;

import com.medical.system.service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {
    private final DemoService demoService;

    @PostMapping("/no-transaction")
    public String testWithoutTransaction(
            @RequestParam String name,
            @RequestParam boolean error) {
        try {
            demoService.createWithoutTransaction(name, error);
            return "Успешно (без транзакции) - все данные сохранены";
        } catch (RuntimeException e) {
            return "Ошибка: " + e.getMessage() + " - проверьте БД, часть данных могла сохраниться";
        }
    }

    @PostMapping("/with-transaction")
    public String testWithTransaction(
            @RequestParam String name,
            @RequestParam boolean error) {
        try {
            demoService.createWithTransaction(name, error);
            return "Успешно (с транзакцией) - все данные сохранены";
        } catch (RuntimeException e) {
            return "Ошибка: " + e.getMessage() + " - все данные откатились благодаря @Transactional";
        }
    }
}