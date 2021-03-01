package com.juno.groovy.executor.mapper;

public interface EntityMapper<D, E> {
  D entityToDto(E entity);

  E dtoToEntity(D dto);

  void updateModel(D dto, E entity);
}
