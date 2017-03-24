package com.ufkoku.ciphersharedpreferences.demo.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PrefsEntry {

    @NonNull
    private String name;

    @Nullable
    private String value;

    @Nullable
    private String encryptedValue;

    public PrefsEntry(@NonNull String name, @Nullable String value, @Nullable String encryptedValue) {
        this.name = name;
        this.value = value;
        this.encryptedValue = encryptedValue;
    }

    @NonNull
    public String getKey() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getEncryptedValue() {
        return encryptedValue;
    }

    public void setEncryptedValue(@Nullable String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrefsEntry entry = (PrefsEntry) o;

        if (!getKey().equals(entry.getKey())) return false;
        if (getValue() != null ? !getValue().equals(entry.getValue()) : entry.getValue() != null)
            return false;
        return getEncryptedValue() != null ? getEncryptedValue().equals(entry.getEncryptedValue()) : entry.getEncryptedValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (getEncryptedValue() != null ? getEncryptedValue().hashCode() : 0);
        return result;
    }

}
