package org.datayoo.correlator.metadata;

import org.datayoo.cepper.metadata.CepperMetadata;

import java.util.LinkedList;
import java.util.List;

public class StateMetadata extends CepperMetadata implements StateDefinition {

  protected int timeout;
  // 是否是否定语义
  protected boolean negative = false;

  protected List<String> actions = new LinkedList<String>();
  // 结果事件定义
  protected EventMetadata eventMetadata;

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  @Override public boolean isNegative() {
    return negative;
  }

  public void setNegative(boolean negative) {
    this.negative = negative;
  }

  public List<String> getActions() {
    return actions;
  }

  public void setActions(List<String> actions) {
    this.actions = actions;
  }

  public EventMetadata getEventMetadata() {
    return eventMetadata;
  }

  public void setEventMetadata(EventMetadata eventMetadata) {
    this.eventMetadata = eventMetadata;
  }
}
