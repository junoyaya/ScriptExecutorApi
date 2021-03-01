package com.juno.groovy.executor.controllers;

import java.util.concurrent.Future;

import com.juno.groovy.executor.models.PendingObject;
import com.juno.groovy.executor.services.GroovyExeServive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class GroovyExeController {
  @Autowired
  private GroovyExeServive GroovyExeService;

  // private HashMap<Integer, Future<String>> futureObjects = new HashMap<>();
  private Future<String> futureObject = null;

  @PostMapping("/exe-script")
  public DeferredResult<ResponseEntity<?>> executeScript(@RequestBody String script) {
    System.out.println("Starting executing script. ");
    long start = System.currentTimeMillis();
    DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
    Future<String> so = GroovyExeService.executeScript(script);
    futureObject = so;
    PendingObject po = new PendingObject(
        start, "Object under creation, please consult on the referenced URL", "/get-result");
    ResponseEntity<PendingObject> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED); // returns pendingObject indicating how to poll the status
    deferredResult.setResult(responseEntity);
    System.out.println("Create Elapsed time: " + (System.currentTimeMillis() - start));
    return deferredResult;
  }

  @GetMapping("/get-result")
  public DeferredResult<ResponseEntity<?>> getResult() throws InterruptedException {
    DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
    try {
      Future<String> futureSO = futureObject; // get future from HashMap
      if (futureSO.isDone()) { // has the future ended?
        // future ended, get and return the result
        String so = futureSO.get();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(so, HttpStatus.CREATED); // returns someObject, the actual result
        deferredResult.setResult(responseEntity);
      } else {
        // future still pending, return same pending as before
        long now = System.currentTimeMillis();
        // TODO not need id?
        PendingObject po = new PendingObject(
            now, "Object still under creation, please consult on the referenced URL", "/get-result");
        ResponseEntity<PendingObject> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED); // returns pendingObject
        deferredResult.setResult(responseEntity);
      }
    } catch (Exception e) {
      // TODO error, return errorObject
      // ErrorObject po = new ErrorObject(
      // id, e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
      // ResponseEntity<ErrorObject> responseEntity = new ResponseEntity<>(po, HttpStatus.I_AM_A_TEAPOT);
      // deferredResult.setResult(responseEntity);
    }
    return deferredResult;
  }

}
