package com.juno.groovy.executor.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {
  private static final long serialVersionUID = -8091879091924046844L;
  private final String jwttoken;
  private final String userName;
  private final String userRole;
  // TODO private final List<String> authorities;
}
