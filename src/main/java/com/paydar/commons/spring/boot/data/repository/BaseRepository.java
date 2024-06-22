package com.paydar.commons.spring.boot.data.repository;

import com.paydar.commons.spring.boot.data.entity.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author m.h paydar
 * @date 6/22/2024 9:13 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
@NoRepositoryBean
public interface BaseRepository<ENTITY extends AbstractEntity, ID>
        extends JpaRepository<ENTITY, ID>, JpaSpecificationExecutor<ENTITY> {

    /**
     * Default delete behaviour that instead of deleting physically, mark entity as deleted
     *
     * @param id of object to be loaded
     */
    @Override
    @Modifying
    @Query("delete from #{#entityName} o  where o.id = :id  and 1=0")
    void deleteById(final @Param("id") ID id);

    @Override
    @Modifying
    @Query("delete from #{#entityName} o  where 1=0")
    void deleteAllById(Iterable<? extends ID> ids );

    @Override
    @Modifying
    @Query("delete from #{#entityName} o  where o = :id and 1=0")
    void delete(final @Param("entity") ENTITY entity);


    @Modifying
    @Query("delete from #{#entityName} o where o.id = :id")
    void removeById(final @Param("id") ID id);

    @Override
    default Optional<ENTITY> findOne(Specification<ENTITY> specification) {
        Page<ENTITY> entities = findAll(specification, PageRequest.of(0, 1));
        return entities.stream().findFirst();
    }


    default Optional<ENTITY> findOne(Specification<ENTITY> querySpecification,
                                     final Specification<ENTITY> permissionSpecification) {
        final Specification<ENTITY> specification = querySpecification.and(permissionSpecification);
        return findOne(specification);
    }

    default Page<ENTITY> findAll(final Specification<ENTITY> querySpecification,
                                 final Specification<ENTITY> permissionSpecification,
                                 final Pageable pageable) {
        final Specification<ENTITY> specification = querySpecification.and(permissionSpecification);
        return findAll(specification, pageable);
    }


    default List<ENTITY> findAll(final Specification<ENTITY> querySpecification,
                                 final Specification<ENTITY> permissionSpecification) {
        final Specification<ENTITY> specification = querySpecification.and(permissionSpecification);
        return findAll(specification);
    }


    default List<ENTITY> findAll(final Specification<ENTITY> querySpecification,
                                 final Specification<ENTITY> permissionSpecification,
                                 final Sort sort) {
        final Specification<ENTITY> specification = querySpecification.and(permissionSpecification);
        return findAll(specification, sort);
    }

}
