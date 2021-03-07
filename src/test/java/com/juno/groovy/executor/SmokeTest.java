package com.juno.groovy.executor;

import static org.assertj.core.api.Assertions.assertThat;

import com.juno.groovy.executor.services.GroovyRunnerServive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * testing most important function before proceeding the further testing
 */
@SpringBootTest
public class SmokeTest {
  @Autowired
  private GroovyRunnerServive controller;

  /*
   * Test the context is creating the controller, with an assertion
   */
  @Test
  public void contextLoads() throws Exception {
    assertThat(controller).isNotNull();
  }
}
