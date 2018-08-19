package org.datayoo.correlator.core;

import org.apache.commons.lang.Validate;
import org.datayoo.correlator.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CepSceneInstance implements SceneInstance {

  protected String name;

  protected SceneInstanceContext sceneInstanceContext;

  protected List<State> states = new LinkedList<State>();

  protected State currentState;

  protected SceneInstanceStateListener sceneInstanceStateListener;

  protected CorrelatorEventListener correlatorEventListener;

  public CepSceneInstance(String name, CorrelatorContext context) {
    Validate.notEmpty(name, "name is empty!");
    Validate.notNull(context, "context is null");

    this.name = name;
    this.sceneInstanceContext = new InnerSceneInstanceContext(context);
  }

  @Override public SceneInstanceContext getSceneInstanceContext() {
    return sceneInstanceContext;
  }

  @Override public State getCurrentState() {
    return currentState;
  }

  @Override public List<State> getStates() {
    return states;
  }

  public void setStates(List<State> states) {
    Validate.notEmpty(states, "states is empty!");
    if (this.states.size() != 0) {
      throw new UnsupportedOperationException("Has been invoked!");
    }
    this.states = states;
    StateTransitionListener listener = new InnerStateTransitionListener();
    for (State state : states) {
      state.setStateTransitionListener(listener);
      if (correlatorEventListener != null)
        state.setCorrelatorEventListener(correlatorEventListener);
    }
    currentState = states.get(0);
  }

  @Override public void transit(List dataSet) {
    for (Object entity : dataSet) {
      currentState.transit(entity);
    }
  }

  @Override public void transit(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSceneInstanceStateListener(
      SceneInstanceStateListener listener) {
    this.sceneInstanceStateListener = listener;
  }

  @Override
  public void setCorrelatorEventListener(CorrelatorEventListener listener) {
    correlatorEventListener = listener;
    if (states != null) {
      for (State state : states) {
        state.setCorrelatorEventListener(correlatorEventListener);
      }
    }
  }

  @Override public void onTick() {
    for (State state : states) {
      state.onTick();
    }
  }

  protected void transitNext(String name) {
    if (currentState == states.get(0))
      sceneInstanceStateListener.onFirstTransit(this);
    State state = findNextState(name);
    // 场景已经执行完成
    if (state == null) {
      transitBack();
    } else {
      currentState = state;
    }
  }

  protected State findNextState(String name) {
    boolean find = false;
    for (State state : states) {
      if (find)
        return state;
      if (state.getName().equals(name)) {
        find = true;
      }
    }
    return null;
  }

  protected void transitBack() {
    for (State state : states) {
      state.reset();
    }
    sceneInstanceContext.clear();
    currentState = states.get(0);
    sceneInstanceStateListener.onBack(this);
  }

  protected class InnerSceneInstanceContext extends HashMap<String, Object>
      implements SceneInstanceContext {

    protected CorrelatorContext env;

    protected Map<String, StateContext> states = new HashMap<String, StateContext>();

    public InnerSceneInstanceContext(CorrelatorContext correlatorContext) {
      this.env = correlatorContext;
    }

    @Override public CorrelatorContext getEnv() {
      return env;
    }

    @Override public Map<String, StateContext> getStates() {
      return states;
    }
  }

  protected class InnerStateTransitionListener
      implements StateTransitionListener {
    @Override
    public void onTransition(String name, boolean success,
        StateContext context) {
      if (success) {
        transitNext(name);
      } else {
        transitBack();
      }
    }
  }
}
