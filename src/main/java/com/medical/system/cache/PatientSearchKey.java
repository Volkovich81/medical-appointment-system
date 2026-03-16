package com.medical.system.cache;

import lombok.Getter;
import java.util.Objects;

@Getter
public class PatientSearchKey {
    private final String specializationName;
    private final int page;
    private final int size;
    private final String sortBy;
    private final String sortDir;

    public PatientSearchKey(String specializationName, int page, int size, String sortBy, String sortDir) {
        this.specializationName = specializationName;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatientSearchKey that = (PatientSearchKey) o;
        return page == that.page
                && size == that.size
                && Objects.equals(specializationName, that.specializationName)
                && Objects.equals(sortBy, that.sortBy)
                && Objects.equals(sortDir, that.sortDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specializationName, page, size, sortBy, sortDir);
    }

    @Override
    public String toString() {
        return String.format("PatientSearchKey{spec='%s', page=%d, size=%d, sort='%s %s'}",
                specializationName, page, size, sortBy, sortDir);
    }
}