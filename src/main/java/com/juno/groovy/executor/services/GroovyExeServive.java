package com.juno.groovy.executor.services;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

@Service
public class GroovyExeServive implements BaseService {

  public Future<String> executeScript(String script) {
    Binding binding = new Binding();
    GroovyShell shell = new GroovyShell(binding);
    // run script and return the result
    // return new AsyncResult<>(so);

    return null;
  }

}
