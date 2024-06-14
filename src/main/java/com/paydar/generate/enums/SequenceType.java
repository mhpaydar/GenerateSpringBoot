package com.paydar.generate.enums;

import java.util.Arrays;

/**
 * @author m.h paydar
 * @date 6/14/2024 9:20 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public enum SequenceType {
    SEQUENCE(1, "SEQUENCE"),
    IDENTITY(2, "IDENTITY");

    final int code;
    final String value;

    SequenceType(final int code, final String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

    public static SequenceType of(final String value) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(value))
                .findFirst().orElse(SequenceType.SEQUENCE);
    }
}
