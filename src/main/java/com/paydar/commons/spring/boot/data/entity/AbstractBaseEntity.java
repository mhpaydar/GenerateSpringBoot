package com.paydar.commons.spring.boot.data.entity;

import lombok.*;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author m.h paydar
 * @date 6/17/2024 4:06 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractBaseEntity<ID>  extends AbstractEntity<ID> implements Serializable, Cloneable  {

//    /**
//     * Number of updates applied to record
//     * Used for handling concurrency so no two update can be applied to two loaded instance of an entity in two
//     * different threads
//     */
//    @Version
//    @Column(nullable = false)
//    private Long version;

//    /**
//     * We do not DELETE a record physically!
//     * if a record must be deleted, we just mark that record as DELETED
//     */
//    @Column(name = "deleted", nullable = false)
//    private boolean deleted = false;
}

