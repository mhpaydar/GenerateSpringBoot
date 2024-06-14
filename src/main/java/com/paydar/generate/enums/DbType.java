package com.paydar.generate.enums;

import java.util.Arrays;

/**
 * @author m.h paydar
 * @date 6/14/2024 5:04 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public enum DbType {
    ORACLE(1,"oracle"),
    MYSQL(2,"mysql");
    final int code;
    final String value;
    DbType(final int code,final String value){
        this.code=code;
        this.value=value;
    }
    public int getCode() {return this.code;}
    public String getValue() {return this.value;}
    public static DbType of(final String value){
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(value))
                .findFirst().orElse(null);
    }
}
