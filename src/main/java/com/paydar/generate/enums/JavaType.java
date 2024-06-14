package com.paydar.generate.enums;

import java.util.Arrays;

/**
 * @author m.h paydar
 * @date 6/14/2024 5:58 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public enum JavaType {
    LONG(1, "Long"),
    INTEGER(2, "Integer"),
    BIG_DECIMAL(3, "BigDecimal"),
    DOUBLE(4, "Double"),
    BOOLEAN(7, "Boolean"),
    CHAR(8, "Char"),
    STRING(9, "String"),
    DATE(13, "Date"),
    LOCAL_DATETIME(14, "LocalDateTime"),
    TIME(15, "LocalTime"),
    UNDEFINE(98, "Undefine"),
    ERROR(99, "Error!")
    ;

    final int code;
    final String value;

    JavaType(final int code, final String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

    public static JavaType of(final String value) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(value))
                .findFirst().orElse(JavaType.STRING);
    }
}
