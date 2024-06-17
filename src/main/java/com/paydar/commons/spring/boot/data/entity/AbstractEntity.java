package com.paydar.commons.spring.boot.data.entity;

/**
 * @author m.h paydar
 * @date 6/17/2024 4:08 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public abstract class AbstractEntity<ID> {

    public abstract ID getId();

    public abstract void setId(final ID id);
}
