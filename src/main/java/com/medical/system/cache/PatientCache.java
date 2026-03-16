package com.medical.system.cache;

import com.medical.system.dto.PatientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PatientCache {

    private final Map<PatientSearchKey, Page<PatientDTO>> cache = new ConcurrentHashMap<>();

    public void put(PatientSearchKey key, Page<PatientDTO> page) {
        cache.put(key, page);
        log.info("✅ [КЭШ] Данные сохранены. Ключ: {}", key);
    }

    public Page<PatientDTO> get(PatientSearchKey key) {
        return cache.get(key);
    }

    public boolean containsKey(PatientSearchKey key) {
        return cache.containsKey(key);
    }

    public void clear() {
        int sizeBefore = cache.size();
        cache.clear();
        log.info("🧹 [КЭШ] Полностью очищен. Удалено элементов: {}", sizeBefore);
    }

    public int size() {
        return cache.size();
    }
}