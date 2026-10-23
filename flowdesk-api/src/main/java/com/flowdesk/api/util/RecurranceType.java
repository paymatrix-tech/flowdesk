package com.flowdesk.api.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecurranceType {
    DEFAULT("Cancelled"), DATE("Certain Date"), COUNT("Certain Number of Payments");

    private String value;

    RecurranceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}