package com.juno.groovy.executor.controllers;

import com.juno.groovy.executor.dtos.UserDataDTO;
import com.juno.groovy.executor.security.JwtRequest;
import com.juno.groovy.executor.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/auth")
@Api(tags = "users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @PostMapping("/signin")
  @ApiOperation(value = "User signin")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 422, message = "Invalid username/password supplied")})
  public ResponseEntity<?> login(@Validated @RequestBody JwtRequest loginRequest) {
    return userService.signin(loginRequest.getUsername(), loginRequest.getPassword());
  }

  @PostMapping("/signup")
  @ApiOperation(value = "User signup")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use")})
  public String signup(@ApiParam("Signup User") @RequestBody UserDataDTO user) {
    return userService.signup(user);
  }

  @PostMapping("/logout")
  @ApiOperation(value = "User logout")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied")})
  public String logout() {
    return userService.logout();
  }

}
