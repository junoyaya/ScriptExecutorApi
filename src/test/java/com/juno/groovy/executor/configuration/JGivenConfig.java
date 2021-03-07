package com.juno.groovy.executor.configuration;

import static java.lang.String.format;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.integration.spring.EnableJGiven;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@EnableJGiven
public class JGivenConfig extends AbstractJGivenConfiguration {

  @Override
  public void configure() {
    setFormatter(HttpStatus.class, (status, annotations) -> format("%s (%d)", status.getReasonPhrase(), status.value()));
  }
}
