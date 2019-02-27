package org.datayoo.correlator.utils;

import org.apache.commons.lang3.Validate;

public class OutTimer {

  protected long timeoutMills;

  protected boolean started = false;

  protected long startTime = 0;

  public OutTimer(long timeoutMills) {
    if (timeoutMills < 0)
      timeoutMills = 0;
    this.timeoutMills = timeoutMills;
  }

  public synchronized void start() {
    startTime = System.currentTimeMillis();
    started = true;
  }

  public synchronized void reset() {
    startTime = 0;
    started = false;
  }

  public boolean isTimeout() {
    if (!started)
      return false;
    if (timeoutMills == 0)
      return false;
    if (System.currentTimeMillis() - startTime > timeoutMills)
      return true;
    return false;
  }
}
