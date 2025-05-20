package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortOrder {
    ASC,
    DESC;

    @JsonCreator
    public static SortOrder fromValue(String value) {
        if (value == null) return ASC;
        switch (value.toLowerCase()) {
            case "asc": return ASC;
            case "desc": return DESC;
        }
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
