package org.datayoo.correlator;

import org.datayoo.correlator.metadata.StateDefinition;
import org.datayoo.correlator.metadata.StateMetadata;

import java.util.List;

public interface Scene extends Transitable {

  String getName();

  int getMaxInstances();

  List<StateDefinition> getStateDefinitions();

  List<SceneInstance> getWorkingInstances();

  List<SceneInstance> getIdleInstances();

  List<SceneInstance> getAllInstances();

  boolean isWorking();

}
