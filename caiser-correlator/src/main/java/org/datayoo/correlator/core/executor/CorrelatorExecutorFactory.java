package org.datayoo.correlator.core.executor;

import org.datayoo.correlator.CorrelatorExecutor;
import org.datayoo.correlator.metadata.SceneMetadata;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class CorrelatorExecutorFactory {

  public static int SIMPLE_CORRELATOR_INSTANCES_LIMIT = 3;

  public static int MAX_THREADS_PER_CPU = 20;

  public static CorrelatorExecutor createCorrelatorExecutor(
      List<SceneMetadata> sceneMetadatas) {
    int instanceCount = getInstanceCount(sceneMetadatas);
    if (instanceCount <= SIMPLE_CORRELATOR_INSTANCES_LIMIT)
      return new SimpleCorrelatorExecutor();

    return null;
  }

  protected static int getInstanceCount(List<SceneMetadata> sceneMetadatas) {
    int instanceCount = 0;
    for (SceneMetadata sceneMetadata : sceneMetadatas) {
      instanceCount += sceneMetadata.getMaxInstances();
    }
    return instanceCount;
  }

  protected static ThreadPoolExecutor createThreadPoolExecutor(
      int instanceCount) {
    int coreThreads =
        Runtime.getRuntime().availableProcessors() * MAX_THREADS_PER_CPU;
    double maxThreads = instanceCount / 2 > coreThreads * 1.5 ?
        coreThreads * 1.5 :
        instanceCount / 2;

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreThreads,
        (int) maxThreads, 60, TimeUnit.SECONDS, new LinkedBlockingDeque());
    return threadPoolExecutor;
  }
}
