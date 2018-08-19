package org.datayoo.correlator;

public interface SceneInstanceStateListener {

  void onFirstTransit(SceneInstance sceneInstance);

  void onBack(SceneInstance sceneInstance);
}
