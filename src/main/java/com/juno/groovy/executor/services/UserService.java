package com.juno.groovy.executor.services;

import javax.transaction.Transactional;

import com.juno.groovy.executor.dtos.UserDataDTO;
import com.juno.groovy.executor.errors.ResponseErrorException;
import com.juno.groovy.executor.mapper.UserMapper;
import com.juno.groovy.executor.models.Role;
import com.juno.groovy.executor.models.User;
import com.juno.groovy.executor.repository.UserRepository;
import com.juno.groovy.executor.security.JwtTokenProvider;

import org.apache.logging.log4j.util.Strings;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService extends BaseEntityService<User, UserDataDTO, Long, UserRepository> {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  public UserService(UserRepository userRepo) {
    super(Mappers.getMapper(UserMapper.class), userRepo);
  }

  @Transactional
  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      return jwtTokenProvider.createToken(username, getRepo().findByUsername(username).getRole());
    } catch (AuthenticationException e) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid username/password supplied");
    }
  }

  @Transactional
  public String signup(UserDataDTO userDto) {
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    User user = createEntity(userDto);
    return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
  }

  @Override
  public String getEntityName() {
    return User.class.getSimpleName();
  }


  @Override
  protected void validateCreationInput(UserDataDTO dto) {
    if (Strings.isBlank(dto.getUsername()) || Strings.isBlank(dto.getPassword())) {
      throw new ResponseErrorException("User name and password must not be null or empty", HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (getRepo().existsByUsername(dto.getUsername())) {
      throw new ResponseErrorException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Override
  protected void setEntityRelation(UserDataDTO dto, User user) {
    // List<Role> roles = new ArrayList<>();
    // List<String> userRoles = dto.getUserRoles();
    // userRoles.forEach(r -> roles.add(Role.valueOf(r.toUpperCase())));
    // user.setRoles(roles);

    user.setRole(Role.valueOf(dto.getUserRole().toUpperCase()));
  }

}
