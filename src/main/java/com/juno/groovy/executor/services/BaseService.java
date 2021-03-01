package com.juno.groovy.executor.services;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

@Service
public interface BaseService {

  Future<String> executeScript(String script);
}


