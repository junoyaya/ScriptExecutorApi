package com.juno.groovy.executor.dtos;

import com.juno.groovy.executor.models.User;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDataDTO extends User {
  // List<String> userRoles;
  String userRole;
}
