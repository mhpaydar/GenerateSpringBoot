package com.paydar.generate.enums;

import java.util.Arrays;

/**
 * @author m.h paydar
 * @date 6/15/2024 11:24 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public enum IndexDbType {
    NORMAL(1, "NORMAL",""),
    FUNCTION_BASED(2, "FUNCTION-BASED NORMAL","//");
    final int code;
    final String value;
    final String comment;
    IndexDbType(final int code, final String value,final String comment) {
        this.code = code;
        this.value = value;
        this.comment=comment;
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }
    public String getComment() {
        return this.comment;
    }
    public static IndexDbType of(final String value) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(value))
                .findFirst().orElse(IndexDbType.NORMAL);
    }
}
