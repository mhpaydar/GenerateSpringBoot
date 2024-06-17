package com.paydar.commons.spring.boot.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author m.h paydar
 * @date 6/17/2024 4:09 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractNonEntity<ID> extends AbstractBaseEntity<ID> {

    /**
     * Generates hash code by ID of object
     *
     * @return hash code of ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * checks if another object is equal to this object
     *
     * @param obj another object
     * @return true if both objects are same instance or two loaded instance of a record (has same id)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractBaseEntity that = (AbstractBaseEntity) obj;
        return Objects.equals(this.getId(), that.getId());
    }
}
