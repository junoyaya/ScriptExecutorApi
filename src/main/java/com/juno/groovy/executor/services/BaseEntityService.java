package com.juno.groovy.executor.services;

import java.util.Optional;

import javax.transaction.Transactional;

import com.juno.groovy.executor.errors.ResponseErrorException;
import com.juno.groovy.executor.mapper.EntityMapper;
import com.juno.groovy.executor.models.AuditableEntity;
import com.juno.groovy.executor.repository.BasicEntityRepo;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class BaseEntityService<E extends AuditableEntity, D extends E, I, R extends BasicEntityRepo<E, I>> {

  private static final Logger logger = LoggerFactory.getLogger(BaseEntityService.class);

  private EntityMapper<D, E> mapper;

  private R repo;

  public BaseEntityService(EntityMapper<D, E> mapper, R repo) {
    super();
    this.mapper = mapper;
    this.repo = repo;
  }

  // @Transactional
  // public Page<D> findEntities(Specification<E> spec, Pageable pageable) {
  // return repo.findAll(spec, pageable).map(entity -> entityToDto(entity));
  // }

  @Transactional
  public D findEntity(I id) {
    E entity = findEntityById(id);
    return entityToDto(entity);
  }

  @Transactional
  public D archiveEntity(I id) {
    E entity = findEntityById(id);
    entity.setArchived(true);
    return entityToDto(repo.save(entity));
  }

  @Transactional
  public E createEntity(D dto) {
    validateCreationInput(dto);
    E entity = getMapper().dtoToEntity(dto);
    setEntityRelation(dto, entity);
    entity = getRepo().save(entity);
    logger.info("Creating new entity " + getEntityName() + " by " + entity.getCreatedBy());
    return entity;
  }

  @Transactional
  public D updateEntity(I id, D dto) {
    E entity = findEntityById(id);
    mapper.updateModel(dto, entity);
    setEntityRelation(dto, entity);
    entity = repo.save(entity);
    logger.info("Updating entity " + getEntityName() + " id: " + id);
    return entityToDto(entity);
  }

  @Transactional
  public void deleteEntity(I id) {
    try {
      Optional<E> entityOpt = repo.findById(id);
      if (entityOpt.isPresent()) {
        E entity = entityOpt.get();
        repo.deleteById(id);
        logger.info("Deleting entity " + getEntityName() + " id: " + id);
      }
    } catch (Exception e) {
      logger.error("Failed deleting entity " + getEntityName() + " id: " + id);
      if (e instanceof ResponseErrorException) {
        throw e;
      } else if (e instanceof ConstraintViolationException) {
        throw new ResponseErrorException(e.getMessage() + "; " + getEntityName() + " is referenced in other tables and cannot be deleted. ID: " + id,
            HttpStatus.INTERNAL_SERVER_ERROR);
      } else {
        throw new ResponseErrorException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  protected E findEntityById(I id) {
    if (id == null) {
      throw new IllegalArgumentException(getEntityName() + " id is not valid");
    }
    Optional<E> opt = repo.findById(id);
    if (!opt.isPresent()) {
      throw new ResponseErrorException(getEntityName() + " does not exist with id: " + id, HttpStatus.NOT_EXTENDED);
    }
    return opt.get();
  }

  public D entityToDto(E entity) {
    D dto = mapper.entityToDto(entity);
    setDtoExtraField(dto, entity);
    return dto;
  }

  protected void setDtoExtraField(D dto, E entity) {}

  protected void setEntityRelation(D dto, E entity) {}

  public abstract String getEntityName();

  protected abstract void validateCreationInput(D dto);


}
