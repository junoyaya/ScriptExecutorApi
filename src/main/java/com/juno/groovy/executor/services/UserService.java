package com.juno.groovy.executor.services;

import javax.transaction.Transactional;

import com.juno.groovy.executor.dtos.UserDataDTO;
import com.juno.groovy.executor.errors.ResponseErrorException;
import com.juno.groovy.executor.mapper.UserMapper;
import com.juno.groovy.executor.models.Role;
import com.juno.groovy.executor.models.User;
import com.juno.groovy.executor.repository.UserRepository;
import com.juno.groovy.executor.security.CurrentUserInformation;
import com.juno.groovy.executor.security.JwtResponse;
import com.juno.groovy.executor.security.JwtTokenProvider;

import org.apache.logging.log4j.util.Strings;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService extends BaseEntityService<User, UserDataDTO, Long, UserRepository> {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  CurrentUserInformation currentUserInformation;

  @Autowired
  public UserService(UserRepository userRepo) {
    super(Mappers.getMapper(UserMapper.class), userRepo);
  }

  @Transactional
  public ResponseEntity<JwtResponse> signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      String jwt = jwtTokenProvider.createToken(username, getRepo().findByUsername(username).getRole());
      return ResponseEntity.ok(new JwtResponse(jwt, currentUserInformation.currentUserId(), currentUserInformation.currentUserRole().toString()));
      // TODO currentUserInformation.currentUserAutorities()
    } catch (AuthenticationException e) {
      logger.error("Invalid username/password supplied for user: " + username);
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
      logger.error("User name and password must not be null or empty");
      throw new ResponseErrorException("User name and password must not be null or empty", HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (getRepo().existsByUsername(dto.getUsername())) {
      logger.error("Username :" + dto.getUsername() + " is already in use");
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

  public User getUserInfo(String currentUserId) {
    // TODO Auto-generated method stub
    return null;
  }

  public String logout() {
    // TODO Auto-generated method stub
    return null;
  }

}
