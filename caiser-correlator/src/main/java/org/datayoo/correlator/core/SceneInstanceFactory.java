package org.datayoo.correlator.core;

import org.datayoo.correlator.*;
import org.datayoo.correlator.metadata.EventMetadata;
import org.datayoo.correlator.metadata.LogicStateMetadata;
import org.datayoo.correlator.metadata.StateDefinition;
import org.datayoo.correlator.metadata.StateMetadata;
import org.datayoo.moql.MapEntry;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.engine.MoqlEngine;

import java.util.LinkedList;
import java.util.List;

public abstract class SceneInstanceFactory {

  static {
    //    MoqlEngine.registFunction();
  }

  public static CepSceneInstance createSceneInstance(String name,
      CorrelatorContext correlatorContext,
      List<StateDefinition> stateDefinitions) {
    CepSceneInstance instance = new CepSceneInstance(name, correlatorContext);
    List<State> states = createStates(stateDefinitions,
        instance.getSceneInstanceContext());
    instance.setStates(states);
    return instance;
  }

  protected static List<State> createStates(
      List<StateDefinition> stateDefinitions, SceneInstanceContext context) {
    List<State> states = new LinkedList<State>();

    for (StateDefinition stateDefinition : stateDefinitions) {
      State state = null;
      if (stateDefinition instanceof StateMetadata) {
        state = createCepState((StateMetadata) stateDefinition, context);
      } else {
        state = createCepLogicState((LogicStateMetadata) stateDefinition,
            context);
      }
      states.add(state);
    }
    return states;
  }

  protected static CepState createCepState(StateMetadata stateMetadata,
      SceneInstanceContext context) {
    CepState cepState = new CepState(stateMetadata, context);
    if (stateMetadata.getActions() != null) {
      cepState.setStateActions(createStateActions(stateMetadata.getActions()));
    }
    if (stateMetadata.getEventMetadata() != null) {
      cepState.setEventCreator(
          creatorEventCreator(stateMetadata.getEventMetadata()));
    }
    return cepState;
  }

  protected static List<Operand> createStateActions(List<String> actions) {
    List<Operand> stateActions = new LinkedList<Operand>();
    for (String action : actions) {
      try {
        Operand operand = MoqlEngine.createOperand(action);
        stateActions.add(operand);
      } catch (MoqlException e) {
        throw new IllegalArgumentException("Create state action failed!", e);
      }
    }
    return stateActions;
  }

  protected static EventCreator creatorEventCreator(
      EventMetadata eventMetadata) {
    GeneralEventCreator eventCreator = new GeneralEventCreator();
    for (MapEntry<String, String> entry : eventMetadata.getEntries()) {
      try {
        Operand value = MoqlEngine.createOperand(entry.getValue());
        eventCreator.addEntry(entry.getKey(), value);
      } catch (MoqlException e) {
        throw new IllegalArgumentException("Create event creator failed!", e);
      }
    }
    return eventCreator;
  }

  protected static CepLogicState createCepLogicState(
      LogicStateMetadata stateMetadata, SceneInstanceContext context) {
    CepLogicState cepLogicState = new CepLogicState(stateMetadata, context);
    List<State> states = createStates(stateMetadata.getStates(), context);
    cepLogicState.setStates(states);
    cepLogicState
        .setStateActions(createStateActions(stateMetadata.getActions()));
    cepLogicState
        .setEventCreator(creatorEventCreator(stateMetadata.getEventMetadata()));
    return cepLogicState;
  }
}
