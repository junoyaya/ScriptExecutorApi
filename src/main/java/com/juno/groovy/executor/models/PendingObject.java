package com.juno.groovy.executor.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PendingObject {

  @JsonProperty("id")
  private final long id;
  @JsonProperty("description")
  private final String description;
  @JsonProperty("url")
  private final String url;

  public PendingObject(long id, String description, String url) {
    this.id = id;
    this.description = description;
    this.url = url;
  }
}
