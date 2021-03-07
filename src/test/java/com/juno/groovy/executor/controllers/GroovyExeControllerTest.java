package com.juno.groovy.executor.controllers;

import com.juno.groovy.executor.configuration.JGivenConfig;
import com.juno.groovy.executor.controllers.stage.GroovyExeControllerStage;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.integration.spring.SimpleSpringScenarioTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@JGivenConfiguration(JGivenConfig.class)
public class GroovyExeControllerTest extends SimpleSpringScenarioTest<GroovyExeControllerStage> {

  private static final String SCRIPT_SUBMITION_ENDPOINT = "/api/run-script";
  private static final String RESULT_FETCHING_ENDPOINT = "/api/get-result";
  private static final String SCRIPT = "Thread.sleep(1000);\r\n" + "return 'Here is the result'";
  private static final String SUBMITED_RESPONSE = "Result under creation, please consult on the referenced URL";

  @Test
  public void should_submit_script_then_return_pending_object_for_user() throws Exception {
    given().a_user("testuser").an_endpoint(SCRIPT_SUBMITION_ENDPOINT)
        .a_groovy_script(SCRIPT);
    when().post_request_is_received();
    then().the_status_is(HttpStatus.ACCEPTED)
        .the_content_contains(SUBMITED_RESPONSE);
  }

  @Test
  public void should_get_result_for_user() throws Exception {
    given().a_user("testuser").a_completed_future_result("5")
        .an_endpoint(SCRIPT_SUBMITION_ENDPOINT)
        .a_groovy_script("2+3");
    when().post_request_is_received();

    given().an_endpoint(RESULT_FETCHING_ENDPOINT);
    when().get_request_is_received();
    then().the_status_is(HttpStatus.CREATED)
        .the_content_contains("5");
  }

  @Test
  public void should_not_get_result_for_wrong_user() throws Exception {
    given().a_user("testuser").a_completed_future_result("5")
        .an_endpoint(SCRIPT_SUBMITION_ENDPOINT)
        .a_groovy_script("2+3");
    when().post_request_is_received();

    given().a_user("another_user").an_endpoint(RESULT_FETCHING_ENDPOINT);
    when().get_request_is_received();
    then().the_status_is(HttpStatus.EXPECTATION_FAILED);
  }

}
