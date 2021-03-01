package com.juno.groovy.executor.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

@Getter
public class ResponseErrorException extends ResponseStatusException {

  private static final long serialVersionUID = 1522011319610066474L;

  public ResponseErrorException(HttpStatus status) {
    super(status);
  }

  public ResponseErrorException(String reason, HttpStatus status, Throwable cause) {
    super(status, reason, cause);
  }

  public ResponseErrorException(String reason, HttpStatus status) {
    super(status, reason);
  }
}
