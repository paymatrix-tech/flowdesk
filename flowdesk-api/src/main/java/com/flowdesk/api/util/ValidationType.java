package com.flowdesk.api.util;

/**
 *
 */
public enum ValidationType {
    ERROR("ERROR"), WARNING("WARNING");

    private String value;

    public String getValue() {
        return this.value;
    }

    private ValidationType(String value) {
        this.value = value;
    }
}