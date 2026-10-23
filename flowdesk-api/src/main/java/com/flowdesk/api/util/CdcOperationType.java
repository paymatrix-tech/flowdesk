package com.flowdesk.api.util;

import lombok.Getter;

@Getter
public enum CdcOperationType {
    C("c"),
    U("u");

    private final String value;

    CdcOperationType(String value) {
        this.value = value;
    }

    public static CdcOperationType fromOp(String op) {
        if (op == null) {
            throw new IllegalArgumentException("Missing op");
        }
        return switch (op.toLowerCase()) {
            case "c" -> C;
            case "u" -> U;
            default -> throw new IllegalArgumentException("Unknown op: " + op);
        };
    }
}
