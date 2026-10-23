package com.flowdesk.api.util;


import com.fasterxml.jackson.annotation.JsonValue;

public enum RecurringFrequency {

    DAILY("Daily"), WEEKLY("Weekly"), BI_WEEKLY("Bi - Weekly"), MONTHLY("Monthly"),
    QUARTERLY("Quarterly"), SEMI_ANNUALLY("Semi - Annually"), YEARLY("Yearly");

    private String value;

    RecurringFrequency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}