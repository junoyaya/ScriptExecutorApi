package com.juno.groovy.executor.services;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

@Service
public class GroovyExeServive {

  private static final Logger logger = LoggerFactory.getLogger(GroovyExeServive.class);


  public Future<String> executeScript(String script) {
    Binding binding = new Binding();
    GroovyShell shell = new GroovyShell(binding);
    // run script and return the result
    // return new AsyncResult<>(so);

    return null;
  }

}
