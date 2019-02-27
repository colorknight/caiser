package org.datayoo.correlator.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.correlator.*;
import org.datayoo.correlator.core.executor.SimpleCorrelatorExecutor;
import org.datayoo.correlator.metadata.StateDefinition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CepScene implements Scene {

  protected String name;

  protected CorrelatorContext correlatorContext;

  protected int maxInstances;

  protected List<StateDefinition> stateDefinitions;

  protected List<TransitionTask> sceneInstances = new LinkedList<TransitionTask>();

  protected List<TransitionTask> idleInstances = new LinkedList<TransitionTask>();

  protected List<TransitionTask> workingInstances = new LinkedList<TransitionTask>();

  protected CorrelatorEventListener correlatorEventListener;

  protected TransitionTask currentInitialStateInstance;

  protected CorrelatorExecutor correlatorExecutor = new SimpleCorrelatorExecutor();

  protected SceneInstanceStateListener sceneInstanceStateListener = new InnerSceneInstanceStateListener();

  protected boolean working = true;

  public CepScene(String name, CorrelatorContext correlatorContext) {
    Validate.notEmpty(name, "name is empty!");
    Validate.notNull(correlatorContext, "correlatorContext is null!");
    this.name = name;
    this.correlatorContext = correlatorContext;
  }

  protected void assginNewSceneInstance() {
    synchronized (sceneInstances) {
      if (sceneInstances.size() < maxInstances) {
        SceneInstance sceneInstance = SceneInstanceFactory
            .createSceneInstance(name, correlatorContext, stateDefinitions);
        sceneInstance.setSceneInstanceStateListener(sceneInstanceStateListener);
        sceneInstance.setCorrelatorEventListener(correlatorEventListener);
        TransitionTask task = new TransitionTask(sceneInstance);
        sceneInstances.add(task);
        currentInitialStateInstance = task;
        workingInstances.add(task);
      } else {
        currentInitialStateInstance = null;
      }
    }
  }

  @Override public String getName() {
    return name;
  }

  @Override public int getMaxInstances() {
    return maxInstances;
  }

  public void setMaxInstances(int maxInstances) {
    Validate.isTrue(maxInstances > 0, "maxInstances could'nt less than 0!");
    this.maxInstances = maxInstances;
  }

  public void setStateDefinitions(List<StateDefinition> stateDefinitions) {
    if (this.stateDefinitions != null) {
      throw new UnsupportedOperationException("Has been invoked!");
    }
    Validate.notEmpty(stateDefinitions, "stateDefinitions is empty!");
//    Validate.isTrue(stateDefinitions.size() > 1,
//        "Scene couldn't less than 2 state!");
    this.stateDefinitions = stateDefinitions;
    assginNewSceneInstance();
  }

  @Override public List<StateDefinition> getStateDefinitions() {
    return stateDefinitions;
  }

  @Override public List<SceneInstance> getWorkingInstances() {
    return getSceneInstances(workingInstances);
  }

  protected List<SceneInstance> getSceneInstances(List<TransitionTask> tasks) {
    List<SceneInstance> sceneInstances = new LinkedList<SceneInstance>();
    for (TransitionTask task : tasks) {
      sceneInstances.add(task.sceneInstance);
    }
    return sceneInstances;
  }

  @Override public List<SceneInstance> getIdleInstances() {
    return getSceneInstances(idleInstances);
  }

  @Override public List<SceneInstance> getAllInstances() {
    return getSceneInstances(sceneInstances);
  }

  @Override public boolean isWorking() {
    return working;
  }

  // always return false
  @Override public void transit(List dataSet) {
    working = true;
    try {
      dispatchSceneInstances(dataSet);
    } finally {
      working = false;
    }
  }

  @Override public void transit(Object entity) {
    throw new UnsupportedOperationException();
  }

  protected void dispatchSceneInstances(List dataSet) {
    List<TransitionTask> workingInstances;
    synchronized (sceneInstances) {
      workingInstances = new LinkedList<TransitionTask>(this.workingInstances);
    }
    for (TransitionTask sceneInstance : workingInstances) {
      sceneInstance.addDataSet(dataSet);
      if (!sceneInstance.isDispatched()) {
        sceneInstance.setDispatched();
        correlatorExecutor.dispatch(sceneInstance);
      }
    }
  }

  protected void recoverySceneInstance(TransitionTask sceneInstance) {
    synchronized (sceneInstances) {
      if (sceneInstances.size() > maxInstances) {
        workingInstances.remove(sceneInstance);
        sceneInstances.remove(sceneInstance);
      } else {
        // the instance count reached the max instances
        if (currentInitialStateInstance == null) {
          currentInitialStateInstance = sceneInstance;
        } else if (currentInitialStateInstance == sceneInstance) {
          return;
        } else {
          workingInstances.remove(sceneInstance);
          idleInstances.add(sceneInstance);
        }
      }
    }
  }

  protected TransitionTask findTransitionTask(SceneInstance sceneInstance) {
    synchronized (sceneInstances) {
      for(TransitionTask transitionTask : sceneInstances) {
        if (transitionTask.sceneInstance == sceneInstance)
          return transitionTask;
      }
    }
    return null;
  }

  protected void assginSceneInstance() {
    synchronized (sceneInstances) {
      if (idleInstances.size() != 0) {
        currentInitialStateInstance = idleInstances.remove(0);
        workingInstances.add(currentInitialStateInstance);
      } else {
        assginNewSceneInstance();
      }
    }
  }

  @Override
  public void setCorrelatorEventListener(CorrelatorEventListener listener) {
    correlatorEventListener = listener;
    synchronized (sceneInstances) {
      for (TransitionTask sceneInstance : sceneInstances) {
        sceneInstance.getSceneInstance().setCorrelatorEventListener(listener);
      }
    }
  }

  @Override public void onTick() {
    List<TransitionTask> workingInstances;
    synchronized (sceneInstances) {
      workingInstances = new LinkedList<TransitionTask>(this.workingInstances);
    }
    for (TransitionTask sceneInstance : workingInstances) {
      sceneInstance.getSceneInstance().onTick();
    }
  }

  public void setCorrelatorExecutor(CorrelatorExecutor correlatorExecutor) {
    if (correlatorExecutor == null)
      return;
    this.correlatorExecutor = correlatorExecutor;
  }

  protected class TransitionTask implements Runnable {

    protected SceneInstance sceneInstance;

    protected volatile boolean dispatched = false;

    protected List<Object> dataList = Collections
        .synchronizedList(new LinkedList<Object>());

    public TransitionTask(SceneInstance sceneInstance) {
      this.sceneInstance = sceneInstance;
    }

    @Override public void run() {
      try {
        while (dataList.size() > 0) {
          List dataSet = (List) dataList.remove(0);
          sceneInstance.transit(dataSet);
        }
      } finally {
        dispatched = false;
      }
    }

    public void addDataSet(List dataSet) {
      dataList.add(dataSet);
    }

    public SceneInstance getSceneInstance() {
      return sceneInstance;
    }

    public void setDispatched() {
      dispatched = true;
    }

    public boolean isDispatched() {
      return dispatched;
    }
  }

  protected class InnerSceneInstanceStateListener
      implements SceneInstanceStateListener {

    @Override public void onFirstTransit(SceneInstance sceneInstance) {
      if (sceneInstance != currentInitialStateInstance.sceneInstance)
        throw new IllegalArgumentException("Logic error!");
      assginSceneInstance();
    }

    @Override public void onBack(SceneInstance sceneInstance) {
      TransitionTask transitionTask = findTransitionTask(sceneInstance);
      recoverySceneInstance(transitionTask);
    }
  }

}
