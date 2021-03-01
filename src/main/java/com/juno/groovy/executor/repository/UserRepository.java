package com.juno.groovy.executor.repository;

import com.juno.groovy.executor.models.User;

public interface UserRepository extends BasicEntityRepo<User, Long> {

  User findByUsername(String username);

  boolean existsByUsername(String username);

}
