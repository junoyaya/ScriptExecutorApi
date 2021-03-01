package com.juno.groovy.executor.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class User extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;
  private String password;
  private boolean isActive;
  private Role role;
  // @ElementCollection
  // @CollectionTable(name = "userRoles", joinColumns = @JoinColumn(name = "userId"))
  // @Column(name = "role")
  // private List<Role> roles;

}