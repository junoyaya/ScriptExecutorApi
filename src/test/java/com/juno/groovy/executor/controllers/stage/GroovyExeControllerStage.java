package com.juno.groovy.executor.controllers.stage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.juno.groovy.executor.controllers.GroovyExeController;
import com.juno.groovy.executor.security.CurrentUserInformation;
import com.juno.groovy.executor.services.GroovyRunnerServive;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@JGivenStage
public class GroovyExeControllerStage {
  @Autowired
  private GroovyExeController groovyExeController;

  @MockBean
  private CurrentUserInformation currentUser;

  @MockBean
  private GroovyRunnerServive service;

  private MockMvc mockMvc;

  private String uri;
  private String script;
  private String username;
  private ResultActions resultActions;

  @BeforeStage
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(groovyExeController).build();
  }

  public GroovyExeControllerStage a_completed_future_result(String expectedResult) {
    CompletableFuture<String> completedFuture = CompletableFuture.completedFuture(expectedResult);
    Map<String, Future<String>> scriptResult = new HashMap<String, Future<String>>();
    scriptResult.put(username, completedFuture);
    Mockito.when(service.runScript(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(scriptResult);
    return this;
  }

  public GroovyExeControllerStage an_endpoint(@Quoted String uri) {
    this.uri = uri;
    return this;
  }

  public GroovyExeControllerStage a_groovy_script(@Quoted String script) {
    this.script = script;
    return this;
  }

  public GroovyExeControllerStage post_request_is_received() throws Exception {
    Mockito.when(currentUser.currentUserId()).thenReturn(username);

    ResultActions resultActions = mockMvc.perform(post(uri)
        .contentType("text/plain")
        .content(script)
        .param("script", script));
    if (script == null) {
      this.resultActions = resultActions;
    } else {
      MvcResult mvcResult = resultActions.andReturn();
      this.resultActions = mockMvc.perform(asyncDispatch(mvcResult));
    }
    return this;
  }

  public GroovyExeControllerStage get_request_is_received() {
    Mockito.when(currentUser.currentUserId()).thenReturn(username);

    try {
      ResultActions resultActions = mockMvc.perform(get(uri));
      MvcResult mvcResult = resultActions.andReturn();
      this.resultActions = mockMvc.perform(asyncDispatch(mvcResult));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this;

  }

  public GroovyExeControllerStage the_status_is(HttpStatus status) throws Exception {
    resultActions.andExpect(status().is(status.value()));
    return this;
  }

  public GroovyExeControllerStage the_content_contains(String content) throws Exception {
    resultActions.andExpect(content().string(org.hamcrest.Matchers.containsString(content)));
    return this;
  }

  public GroovyExeControllerStage a_user(@Quoted String username) {
    this.username = username;
    return this;
  }

}
