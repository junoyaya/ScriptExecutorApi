package com.juno.groovy.executor.mapper;

import com.juno.groovy.executor.dtos.UserDataDTO;
import com.juno.groovy.executor.models.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDataDTO, User> {
  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "archived", ignore = true)
  })
  @Override
  User dtoToEntity(UserDataDTO dto);

  @Override
  UserDataDTO entityToDto(User entity);

  /**
   * copy values from dto to entity, ignore relation objects.
   */
  @Mappings({
      @Mapping(target = "archived", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createdBy", ignore = true),
      @Mapping(target = "lastModifiedBy", ignore = true),
      @Mapping(target = "createdDate", ignore = true),
      @Mapping(target = "lastModifiedDate", ignore = true)
  })
  void updateModel(UserDataDTO dto, @MappingTarget User entity);
}
