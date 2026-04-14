package com.medical.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Tag(name = "Race Condition", description = "Демонстрация состояния гонки и потокобезопасного решения")
@RestController
@RequestMapping("/race")
public class RaceConditionController {

    private long nonAtomicCounter = 0;
    private final AtomicLong atomicCounter = new AtomicLong(0);

    @Operation(summary = "Непотокобезопасный инкремент (race condition)")
    @PostMapping("/increment/non-atomic")
    public String incrementNonAtomic() {
        nonAtomicCounter++;
        return "Non-atomic counter value: " + nonAtomicCounter;
    }

    @Operation(summary = "Потокобезопасный инкремент (AtomicLong)")
    @PostMapping("/increment/atomic")
    public String incrementAtomic() {
        long newValue = atomicCounter.incrementAndGet();
        return "Atomic counter value: " + newValue;
    }

    @Operation(summary = "Сбросить оба счётчика в 0")
    @PostMapping("/reset")
    public String resetCounters() {
        nonAtomicCounter = 0;
        atomicCounter.set(0);
        return "Counters reset to 0";
    }

    @Operation(summary = "Запустить нагрузку для демонстрации race condition (50 потоков по 100 запросов)")
    @PostMapping("/test")
    public String runRaceConditionTest() throws InterruptedException {
        resetCounters();
        int numberOfThreads = 50;
        int incrementsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        log.info("Запуск теста: {} потоков, по {} инкрементов каждый", numberOfThreads, incrementsPerThread);
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    nonAtomicCounter++;
                    atomicCounter.incrementAndGet();
                }
            });
        }
        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);
        if (finished) {
            long expected = (long) numberOfThreads * incrementsPerThread;
            String result = String.format(
                    "Тест завершён.%nОжидаемое значение: %d%n" +
                            "Непотокобезопасный счётчик (race condition): %d (потеряно %d)%n" +
                            "Потокобезопасный счётчик (AtomicLong): %d (потеряно %d)",
                    expected,
                    nonAtomicCounter, expected - nonAtomicCounter,
                    atomicCounter.get(), expected - atomicCounter.get()
            );
            log.info(result);
            return result;
        } else {
            return "Тест не завершился за отведённое время.";
        }
    }

    @Operation(summary = "Получить текущие значения счётчиков")
    @GetMapping("/values")
    public String getCurrentValues() {
        return String.format("Non-atomic: %d, Atomic: %d", nonAtomicCounter, atomicCounter.get());
    }
}