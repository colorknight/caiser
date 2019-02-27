package org.datayoo.correlator.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.correlator.*;
import org.datayoo.correlator.action.SetProperty;
import org.datayoo.correlator.core.executor.CorrelatorExecutorFactory;
import org.datayoo.correlator.core.executor.ThreadPoolCorrelatorExecutor;
import org.datayoo.correlator.metadata.SceneMetadata;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.util.StringFormater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class CepCorrelator implements Correlator {

  protected static Logger logger = LoggerFactory
      .getLogger(CepCorrelator.class.getName());

  protected List<Scene> scenes = new LinkedList<Scene>();

  protected CorrelatorContext correlatorContext = new GeneralCorrelatorContext();

  protected CorrelatorExecutor correlatorExecutor;

  protected Ticker ticker = new Ticker();

  protected Set<CorrelatorEventListener> listeners = new HashSet<CorrelatorEventListener>();

  protected boolean shutdown = false;

  static {
    MoqlEngine
        .registFunction(SetProperty.FUNCTION_NAME, SetProperty.class.getName());
  }

  public CepCorrelator(List<SceneMetadata> sceneMetadatas) {
    Validate.notEmpty(sceneMetadatas, "sceneMetadatas is empty!");
    correlatorExecutor = CorrelatorExecutorFactory
        .createCorrelatorExecutor(sceneMetadatas);
    createScenes(sceneMetadatas);
    ticker.start();
  }

  protected void createScenes(List<SceneMetadata> sceneMetadatas) {
    CorrelatorEventListener eventListener = new InnerCorrelatorEventListener();
    for (SceneMetadata sceneMetadata : sceneMetadatas) {
      CepScene scene = new CepScene(sceneMetadata.getName(), correlatorContext);
      scene.setMaxInstances(sceneMetadata.getMaxInstances());
      scene.setStateDefinitions(sceneMetadata.getStates());
      if (correlatorExecutor != null) {
        scene.setCorrelatorExecutor(correlatorExecutor);
      }
      scene.setCorrelatorEventListener(eventListener);
      scenes.add(scene);
    }
  }

  public CepCorrelator(List<SceneMetadata> sceneMetadatas,
      CorrelatorContext correlatorContext) {
    Validate.notEmpty(sceneMetadatas, "sceneMetadatas is empty!");
    Validate.notNull(correlatorContext, "correlatorContext is null!");
    this.correlatorContext = correlatorContext;
    correlatorExecutor = CorrelatorExecutorFactory
        .createCorrelatorExecutor(sceneMetadatas);
    createScenes(sceneMetadatas);
    ticker.start();
  }

  public CepCorrelator(List<SceneMetadata> sceneMetadatas,
      CorrelatorContext correlatorContext,
      ThreadPoolExecutor threadPoolExecutor) {
    Validate.notEmpty(sceneMetadatas, "sceneMetadatas is empty!");
    Validate.notNull(correlatorContext, "correlatorContext is null!");
    Validate.notNull(threadPoolExecutor, "threadPoolExecutor is null!");
    this.correlatorContext = correlatorContext;
    correlatorExecutor = new ThreadPoolCorrelatorExecutor(threadPoolExecutor);
    createScenes(sceneMetadatas);
    ticker.start();
  }

  @Override public CorrelatorContext getCorrelatorContext() {
    return correlatorContext;
  }

  @Override public List<Scene> getScenes() {
    return scenes;
  }

  @Override public void transit(List dataSet) {
    if (shutdown)
      throw new UnsupportedOperationException(
          "The correlator has been shutdown!");
    for (Scene scene : scenes) {
      scene.transit(dataSet);
    }
  }

  @Override public void registFunction(String functionName, String className) {
    MoqlEngine.registFunction(functionName, className);
  }

  @Override
  public void addCorrelatorEventListener(CorrelatorEventListener listener) {
    if (listener == null)
      return;
    listeners.add(listener);
  }

  @Override
  public CorrelatorEventListener removeCorrelatorEventListener(
      CorrelatorEventListener listener) {
    if (listener == null)
      return null;
    if (listeners.remove(listener))
      return listener;
    return null;
  }

  @Override public synchronized void shutdown() {
    if (shutdown)
      return;
    try {
      if (correlatorExecutor != null)
        correlatorExecutor.shutdown();
      ticker.shutdown();
    } finally {
      shutdown = true;
    }
  }

  @Override public boolean isShutdown() {
    return shutdown;
  }

  protected class InnerCorrelatorEventListener
      implements CorrelatorEventListener {

    @Override public void onEvent(CorrelatorEvent event) {
      for (CorrelatorEventListener listener : listeners) {
        try {
          listener.onEvent(event);
        } catch (Throwable t) {
          logger.warn(t.getMessage(), t);
        }
      }
    }
  }

  protected class Ticker extends Thread {

    protected boolean running = true;

    public Ticker() {
      super("Correlator Ticker");
    }

    @Override public void run() {
      while (running) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          logger.warn("Ticker thread is interrupted!", e);
        }

        for (Scene scene : scenes) {
          try {
            scene.onTick();
          } catch (Throwable t) {
            logger.warn(StringFormater
                .format("Scene named '{}' executes 'ontick' failed!",
                    scene.getName()), t);
          }
        }

      }
    }

    public void shutdown() {
      running = false;
    }
  }
}
