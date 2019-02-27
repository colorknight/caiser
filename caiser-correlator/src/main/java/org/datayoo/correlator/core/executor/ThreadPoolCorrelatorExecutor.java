package org.datayoo.correlator.core.executor;

import org.apache.commons.lang3.Validate;
import org.datayoo.correlator.CorrelatorExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolCorrelatorExecutor implements CorrelatorExecutor {

  protected ThreadPoolExecutor threadPoolExecutor;

  public ThreadPoolCorrelatorExecutor(ThreadPoolExecutor threadPoolExecutor) {
    Validate.notNull(threadPoolExecutor, "threadPoolExecutor is null!");
    this.threadPoolExecutor = threadPoolExecutor;
  }

  @Override public void dispatch(Runnable runnable) {
    threadPoolExecutor.execute(runnable);
  }

  @Override public void shutdown() {
    threadPoolExecutor.shutdownNow();
  }

}
