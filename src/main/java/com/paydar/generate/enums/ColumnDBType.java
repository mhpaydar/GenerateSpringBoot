package com.paydar.generate.enums;

import java.util.Arrays;

/**
 * @author m.h paydar
 * @date 6/14/2024 5:58 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public enum ColumnDBType {
    NUMBER(1, "NUMBER"),
    INTEGER(2, "INTEGER"),
    FLOAT(3, "FLOAT"),
    DECIMAL(4, "DECIMAL"),
    INT(5, "INT"),
    BIGINT(6, "BIGINT"),
    SMALLINT(7, "SMALLINT"),
    CHAR(8, "CHAR"),
    VARCHAR(9, "VARCHAR"),
    NVARCHAR(10, "NVARCHAR"),
    VARCHAR2(11, "VARCHAR2"),
    NVARCHAR2(12, "NVARCHAR2"),
    DATE(13, "DATE"),
    TIMESTAMP(14, "TIMESTAMP"),
    DATETIME(15, "DATETIME");

    final int code;
    final String value;

    ColumnDBType(final int code, final String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

    public static ColumnDBType of(final String value) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(value))
                .findFirst().orElse(ColumnDBType.VARCHAR2);
    }
}
