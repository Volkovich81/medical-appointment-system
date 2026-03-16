package com.medical.system.cache;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.medical.system.dto.PatientDTO;

@Component
public class PatientCache {

    private final Map<PatientSearchKey, Page<PatientDTO>> cache = new ConcurrentHashMap<>();

    public void put(PatientSearchKey key, Page<PatientDTO> page) {
        cache.put(key, page);
        System.out.println("✅ [КЭШ] Данные сохранены. Ключ: " + key);
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
        System.out.println("🧹 [КЭШ] Полностью очищен. Удалено элементов: " + sizeBefore);
    }

    public int size() {
        return cache.size();
    }
}