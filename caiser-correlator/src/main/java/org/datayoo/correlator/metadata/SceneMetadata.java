package org.datayoo.correlator.metadata;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SceneMetadata implements Serializable {

  protected String name;

  protected int maxInstances = 1;

  protected int deadlockTimeout = 3600000;//  mills

  protected List<StateDefinition> states = new LinkedList<StateDefinition>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxInstances() {
    return maxInstances;
  }

  public void setMaxInstances(int maxInstances) {
    this.maxInstances = maxInstances;
  }

  public int getDeadlockTimeout() {
    return deadlockTimeout;
  }

  public void setDeadlockTimeout(int deadlockTimeout) {
    this.deadlockTimeout = deadlockTimeout;
  }

  public List<StateDefinition> getStates() {
    return states;
  }

  public void setStates(List<StateDefinition> states) {
    this.states = states;
  }
}
