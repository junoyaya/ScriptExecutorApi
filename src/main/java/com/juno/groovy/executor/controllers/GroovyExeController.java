package com.juno.groovy.executor.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.juno.groovy.executor.models.PendingObject;
import com.juno.groovy.executor.security.CurrentUserInformation;
import com.juno.groovy.executor.services.GroovyRunnerServive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class GroovyExeController {

  private static final Logger logger = LoggerFactory.getLogger(GroovyExeController.class);

  @Autowired
  private GroovyRunnerServive groovyRunnerService;

  @Autowired
  private CurrentUserInformation currentUser;

  private Map<String, Future<String>> futureObject = new HashMap<>();

  @PostMapping("/api/run-script")
  @ApiOperation(value = "Send the script for executing")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied")})
  public DeferredResult<ResponseEntity<?>> runScript(@RequestBody String script) {
    long start = System.currentTimeMillis();
    String currentUserId = currentUser.currentUserId();
    logger.info("Starting to run script. User: " + currentUserId);

    Map<String, Future<String>> result = groovyRunnerService.runScript(script, currentUserId);
    futureObject = result;

    PendingObject po = new PendingObject(
        start, "Result under creation, please consult on the referenced URL", "/api/get-result");
    ResponseEntity<PendingObject> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED); // returns pendingObject indicating how to poll the status
    DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
    deferredResult.setResult(responseEntity);

    logger.info("Create Elapsed time: " + (System.currentTimeMillis() - start));
    return deferredResult;
  }

  @GetMapping("/api/get-result")
  @ApiOperation(value = "Get the executing result")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied")})
  public DeferredResult<ResponseEntity<?>> getResult() throws InterruptedException {
    DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
    String currentUserId = currentUser.currentUserId();
    logger.info("Fetching result of script. User: " + currentUserId);
    try {
      Future<String> futureResult = futureObject.get(currentUserId);
      if (futureResult.isDone()) {
        // future ended, get and return the result
        String result = futureResult.get();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(result, HttpStatus.CREATED); // returns the actual result
        deferredResult.setResult(responseEntity);
        logger.info("Fetched result of script. User: " + currentUserId);
      } else {
        // future still pending, return same pending as before
        long now = System.currentTimeMillis();
        PendingObject po = new PendingObject(
            now, "Result still under creation, please consult on the referenced URL", "/api/get-result");
        ResponseEntity<PendingObject> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED); // returns pendingObject
        deferredResult.setResult(responseEntity);
      }
    } catch (Exception e) {
      ResponseEntity<String> responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
      deferredResult.setResult(responseEntity);
      logger.error("Error ocurring when fetching result of script. User: " + currentUserId);
    }
    return deferredResult;
  }

}
