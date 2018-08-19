package org.datayoo.correlator.core;

import org.apache.commons.lang.Validate;
import org.datayoo.correlator.*;
import org.datayoo.correlator.metadata.StateDefinition;
import org.datayoo.correlator.utils.OutTimer;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.Operand;
import org.datayoo.moql.RecordSet;

import java.util.List;

public abstract class AbstractState<T extends StateDefinition>
    implements State {

  protected T metadata;

  protected StateStatus stateStatus = StateStatus.WAITING;

  protected List<Operand> stateActions;

  protected EventCreator eventCreator;

  protected EntityMapImpl entityMap = new EntityMapImpl();

  protected InnerStateContext context;

  protected StateTransitionListener stateTransitionListener;

  protected CorrelatorEventListener correlatorEventListener;

  protected OutTimer outTimer;

  public AbstractState(T metadata, SceneInstanceContext context) {
    Validate.notNull(metadata, "metadata is null!");
    Validate.notNull(context, "context is null!");

    this.metadata = metadata;
    this.context = new InnerStateContext(context);
    context.getStates().put(metadata.getName(), this.context);
    entityMap.putEntity(CorrelatorConstants.RK_GCTX, context.getEnv());
    entityMap.putEntity(CorrelatorConstants.RK_CTX, context);
    outTimer = new OutTimer(metadata.getTimeout() * 1000);
  }

  @Override public String getName() {
    return metadata.getName();
  }

  @Override public StateContext getStateContext() {
    return context;
  }

  @Override public List<Operand> getStateActions() {
    return stateActions;
  }

  public void setStateActions(List<Operand> stateActions) {
    this.stateActions = stateActions;
  }

  @Override public EventCreator getEventCreator() {
    return eventCreator;
  }

  public void setEventCreator(EventCreator eventCreator) {
    this.eventCreator = eventCreator;
  }

  @Override public StateTransitionListener getStateTransitionListener() {
    return stateTransitionListener;
  }

  public void setStateTransitionListener(
      StateTransitionListener stateTransitionListener) {
    this.stateTransitionListener = stateTransitionListener;
  }

  @Override public void transit(List dataSet) {
    throw new UnsupportedOperationException();
  }

  @Override public synchronized void transit(Object entity) {
    if (stateStatus == StateStatus.FINISHED)
      return;
    if (stateStatus == StateStatus.WAITING) {
      outTimer.start();
      stateStatus = StateStatus.RUNNING;
    }
    innerTransit(entity);
  }

  protected abstract void innerTransit(Object entity);

  @Override public synchronized void reset() {
    outTimer.reset();
    entityMap.removeEntity(CorrelatorConstants.RK_RECORDSET);
    entityMap.removeEntity(CorrelatorConstants.RK_ORIGINALDATA);
    innerReset();
    stateStatus = StateStatus.WAITING;
  }

  protected abstract void innerReset();

  protected synchronized void onStateTransition(boolean success,
      RecordSet recordSet, List originalData) {
    if (stateStatus != StateStatus.RUNNING)
      return;
    try {
      stateStatus = StateStatus.FINISHED;
      outTimer.reset();
      if (success) {
        if (recordSet != null) {
          context.setRecordSet(recordSet);
          context.setOriginalData(originalData);
          entityMap.putEntity(CorrelatorConstants.RK_RECORDSET, recordSet);
          entityMap
              .putEntity(CorrelatorConstants.RK_ORIGINALDATA, originalData);
        }
        doStateActions(entityMap);
        createCorrelatorEvent(entityMap);
      }
    } finally {
      if (stateTransitionListener != null) {
        stateTransitionListener
            .onTransition(metadata.getName(), success, context);
      }
    }
  }

  protected void doStateActions(EntityMap entityMap) {
    if (stateActions == null)
      return;
    for (Operand stateAction : stateActions) {
      stateAction.operate(entityMap);
    }
  }

  protected void createCorrelatorEvent(EntityMap entityMap) {
    if (eventCreator == null)
      return;
    CorrelatorEvent event = eventCreator.create(entityMap);
    if (correlatorEventListener != null)
      correlatorEventListener.onEvent(event);
  }

  @Override public void onTick() {
    if (outTimer.isTimeout()) {
      // 当是否定语义时
      if (metadata.isNegative()) {
        onStateTransition(true, null, null);
      } else {
        onStateTransition(false, null, null);
      }
    }
  }

  @Override
  public void setCorrelatorEventListener(
      CorrelatorEventListener correlatorEventListener) {
    this.correlatorEventListener = correlatorEventListener;
  }

  protected class InnerStateContext implements StateContext {

    protected SceneInstanceContext context;

    protected RecordSet recordSet;

    protected List originalData;

    public InnerStateContext(SceneInstanceContext context) {
      this.context = context;
    }

    @Override public String getName() {
      return AbstractState.this.metadata.getName();
    }

    @Override public StateStatus getStateStatus() {
      return AbstractState.this.stateStatus;
    }

    @Override public RecordSet getRecordSet() {
      return recordSet;
    }

    public void setRecordSet(RecordSet recordSet) {
      this.recordSet = recordSet;
    }

    public void setOriginalData(List originalData) {
      this.originalData = originalData;
    }

    @Override public List getOriginalData() {
      return originalData;
    }

    @Override public void clear() {
      recordSet = null;
      originalData = null;
    }
  }
}
