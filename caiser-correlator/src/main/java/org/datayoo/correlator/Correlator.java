package org.datayoo.correlator;

import java.util.List;

public interface Correlator {

  CorrelatorContext getCorrelatorContext();

  List<Scene> getScenes();

  void shutdown();

  boolean isShutdown();

  void transit(List dataSet);

  void addCorrelatorEventListener(CorrelatorEventListener listener);

  CorrelatorEventListener removeCorrelatorEventListener(
      CorrelatorEventListener listener);

  void registFunction(String functionName, String className);

}
