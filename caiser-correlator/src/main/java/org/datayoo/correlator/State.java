package org.datayoo.correlator;

import org.datayoo.moql.Operand;

import java.util.List;

public interface State extends Transitable {

  String getName();

  StateContext getStateContext();

  List<Operand> getStateActions();

  EventCreator getEventCreator();

  void reset();

  void setStateTransitionListener(StateTransitionListener listerner);

  StateTransitionListener getStateTransitionListener();

}
