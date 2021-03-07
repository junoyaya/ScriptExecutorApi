package com.juno.groovy.executor.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import com.juno.groovy.executor.security.CurrentUserInformation;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

@Service
public class GroovyRunnerServive implements ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(GroovyRunnerServive.class);

  private GroovyShell groovyShell;
  private Binding groovyBinding;
  private ApplicationContext applicationContext;

  @Autowired
  CurrentUserInformation currentUserInformation;

  @Autowired
  Executor taskExecutor;

  private Map<String, Future<String>> map = new ConcurrentHashMap();

  @PostConstruct
  public void init() {
    groovyBinding = createBinding();
    groovyShell = createGroovyShell();
  }

  private GroovyShell createGroovyShell() {
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader());
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.setSourceEncoding("utf-8");
    compilerConfiguration.setScriptBaseClass(Script.class.getName());

    return new GroovyShell(groovyClassLoader, groovyBinding, compilerConfiguration);
  }

  // @Async // TODO?
  public Map<String, Future<String>> runScript(String scriptContent, String currentUserId) {
    logger.info("Running script. User: " + currentUserId);

    Script script = groovyShell.parse(scriptContent);
    String result = String.valueOf(script.run());
    map.put(currentUserId, new AsyncResult<>(result));

    logger.info("Finish running script. User: " + currentUserId);
    return map;

    // Callable<Map<String, Future<String>>> callable = () -> {
    // Map<String, Future<String>> map = new HashMap<>();
    // logger.info("Processing script with user " + currentUserId);
    //
    // Script script = groovyShell.parse(scriptContent);
    // String result = String.valueOf(script.run());
    // map.put(currentUserId, new AsyncResult<>(result));
    //
    // logger.info("Finish script with user " + currentUserId);
    // return map;
    // };
    //
    // Future<Map<String, Future<String>>> submit = executorService.submit(callable);
    // executorService.shutdown();
    // try {
    // return submit.get();
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ExecutionException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return null;
  }

  private Binding createBinding() {
    OutputStream outputStream = new ByteArrayOutputStream();
    var binding = new Binding();
    binding.setVariable("applicationContext", applicationContext);
    binding.setProperty("out", new PrintStream(outputStream, true));
    return binding;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
