package org.datayoo.correlator;

import java.util.List;

public interface SceneInstance extends Transitable {

  SceneInstanceContext getSceneInstanceContext();

  State getCurrentState();

  List<State> getStates();

  void setSceneInstanceStateListener(SceneInstanceStateListener listener);

}
