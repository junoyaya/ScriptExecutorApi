package com.juno.groovy.executor.models;

import org.springframework.security.core.GrantedAuthority;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum Role implements GrantedAuthority {
  ADMIN, USER;

  public String getAuthority() {
    return name();
  }

  public String toString() {
    return name();
  }

}
