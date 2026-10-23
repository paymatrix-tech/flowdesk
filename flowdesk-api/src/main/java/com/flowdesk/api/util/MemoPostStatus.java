package com.flowdesk.api.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemoPostStatus {
    POSTED("POSTED"),
    NOT_POSTED("NOT_POSTED"),
    SUCCESS("SUCCESS"),
    PARTIAL_SUCCESS("PARTIAL_SUCCESS"),
    FAILURE("FAILURE"),
    NOT_APPLICABLE("NOT_APPLICABLE"),
    NA_RETURN_INITIATED("NA_RETURN_INITIATED"),
    NA_RECEIVED_REVERSAL("NA_RECEIVED_REVERSAL"),
    SUBMITTED("SUBMITTED");

    private String value;

    MemoPostStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static MemoPostStatus fromValue(String text) {
        for (MemoPostStatus m : MemoPostStatus.values()) {
            if (String.valueOf(m.value).equals(text)) {
                return m;
            }
        }
        return null;
    }
}