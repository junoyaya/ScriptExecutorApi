package com.juno.groovy.executor.security;

import java.util.ArrayList;
import java.util.List;

import com.juno.groovy.executor.models.Role;
import com.juno.groovy.executor.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserInformation {

  @Autowired
  private UserService userService;

  public String currentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return "";
    }

    Object principal = authentication.getPrincipal();

    if (principal == null) {
      return "";
    } else if (principal instanceof String) {
      return authentication.getName();
    } else if (principal instanceof User) {
      return ((User) principal).getUsername();
    } else if (principal instanceof UserDetails) {
      return ((UserDetails) principal).getUsername();
    }

    return "";
  }

  public Object currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return authentication == null ? "" : authentication.getPrincipal();
  }

  public Role currentUserRole() {
    return userService.getUserInfo(currentUserId()).getRole();
  }

  public List<String> currentUserAutorities() {
    List<String> autorities = new ArrayList<>();
    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // if (authentication == null) {
    // return autorities;
    // }
    //
    // for (GrantedAuthority auth : authentication.getAuthorities()) {
    // autorities.add(auth.getAuthority());
    // }
    return autorities;
  }

}
