package org.datayoo.correlator;

public interface CorrelatorExecutor {

  void dispatch(Runnable runnable);

  void shutdown();

}
