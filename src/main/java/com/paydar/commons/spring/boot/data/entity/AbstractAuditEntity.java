package com.paydar.commons.spring.boot.data.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author m.h paydar
 * @date 6/17/2024 4:05 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditEntity<ID> extends AbstractBaseEntity<ID> {

    /**
     * ID of user who created this entity
     */
    @CreatedBy
    @Column(name = "FK_USER_REG_ID", nullable = false, updatable = false)
    private Long userRegId;

    /**
     * Date and time of creation this entity
     */
    @CreatedDate
    @Column(name = "USER_REG_DATE", nullable = false, updatable = false, columnDefinition = "DATE")
    private LocalDateTime creationDateTime;

    /**
     * ID of user who did last update to this entity
     */
    @LastModifiedBy
    @Column(name = "FK_USER_UPDATE_ID", insertable = false)
    private Long userUpdateId;

    /**
     * Last date and time of modification
     */
    @LastModifiedDate
    @Column(name = "USER_UPDATE_DATE", insertable = false, columnDefinition = "DATE")
    private LocalDateTime lastModificationDateTime;

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

