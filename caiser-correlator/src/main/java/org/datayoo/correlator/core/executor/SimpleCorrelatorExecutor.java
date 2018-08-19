package org.datayoo.correlator.core.executor;

import org.datayoo.correlator.CorrelatorExecutor;

public class SimpleCorrelatorExecutor implements CorrelatorExecutor {

  @Override public void dispatch(Runnable runnable) {
    runnable.run();
  }

  @Override public void shutdown() {

  }

}
