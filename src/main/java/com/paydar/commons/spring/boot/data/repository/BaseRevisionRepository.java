package com.paydar.commons.spring.boot.data.repository;

import com.paydar.commons.spring.boot.data.entity.AbstractEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

/**
 * @author m.h paydar
 * @date 6/22/2024 8:35 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
@NoRepositoryBean
public interface BaseRevisionRepository<ENTITY extends AbstractEntity, ID>
        extends BaseRepository<ENTITY, ID>, RevisionRepository<ENTITY, ID, Long> {

}
