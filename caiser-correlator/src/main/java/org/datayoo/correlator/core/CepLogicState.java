package org.datayoo.correlator.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.correlator.*;
import org.datayoo.correlator.metadata.LogicOperator;
import org.datayoo.correlator.metadata.LogicStateMetadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CepLogicState extends AbstractState<LogicStateMetadata>
    implements LogicState {

  protected LogicOperator logicOperator;

  protected List<State> states;

  protected Map<String, Boolean> stateStatus = new HashMap<String, Boolean>();

  public CepLogicState(LogicStateMetadata metadata,
      SceneInstanceContext context) {
    super(metadata, context);
    this.logicOperator = metadata.getOperator();
  }

  public List<State> getStates() {
    return states;
  }

  public void setStates(List<State> states) {
    Validate.notEmpty(states, "states is empty!");
    if (this.states != null) {
      throw new UnsupportedOperationException("Has been invoked!");
    }
    this.states = states;
    StateTransitionListener listener = new InnerStateTransitionListener();
    for (State state : states) {
      state.setStateTransitionListener(listener);
    }
  }

  @Override public void innerTransit(Object entity) {
    for (State state : states) {
      state.transit(entity);
    }
  }

  protected void onStateTransition(String name, boolean success) {
    if (logicOperator == LogicOperator.AND) {
      if (metadata.isNegative() && success || !success) {
        onStateTransition(false, null, null);
      } else {
        stateStatus.put(name, success);
        if (stateStatus.size() == states.size()) {
          onStateTransition(true, null, null);
        }
      }
    } else {
      if (metadata.isNegative() && !success || success) {
        onStateTransition(true, null, null);
      } else {
        stateStatus.put(name, success);
        if (stateStatus.size() == states.size()) {
          onStateTransition(false, null, null);
        }
      }
    }
  }

  @Override protected void innerReset() {
    stateStatus.clear();
    for (State state : states) {
      state.reset();
    }
  }

  @Override public void onTick() {
    if (outTimer.isTimeout()) {
      // 当是否定语义时
      if (metadata.isNegative()) {
        onStateTransition(true, null, null);
      } else {
        onStateTransition(false, null, null);
      }
    } else {
      for (State state : states) {
        state.onTick();
      }
    }
  }

  @Override
  public void setCorrelatorEventListener(
      CorrelatorEventListener correlatorEventListener) {
    super.setCorrelatorEventListener(correlatorEventListener);
    for (State state : states) {
      state.setCorrelatorEventListener(correlatorEventListener);
    }
  }

  protected class InnerStateTransitionListener
      implements StateTransitionListener {
    @Override
    public void onTransition(String name, boolean success,
        StateContext context) {
      onStateTransition(name, true);
    }
  }

}
