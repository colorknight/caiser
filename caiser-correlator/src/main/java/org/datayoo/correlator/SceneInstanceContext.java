package org.datayoo.correlator;

import java.util.Map;

public interface SceneInstanceContext extends Map<String, Object> {

  /**
   * get the correlator property enviroment
   * @return the correlatorContext
   */
  CorrelatorContext getEnv();

  /**
   * get the all state contextes
   * @return the map of state context
   */
  Map<String, StateContext> getStates();

}
