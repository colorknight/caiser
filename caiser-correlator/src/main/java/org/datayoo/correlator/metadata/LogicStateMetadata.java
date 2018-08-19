package org.datayoo.correlator.metadata;

import java.util.LinkedList;
import java.util.List;

public class LogicStateMetadata implements StateDefinition {

  protected String name;

  protected LogicOperator operator;

  protected int timeout;
  // 是否是否定语义
  protected boolean negative = false;

  protected List<StateDefinition> states = new LinkedList<StateDefinition>();

  protected List<String> actions = new LinkedList<String>();

  // 结果事件定义
  protected EventMetadata eventMetadata;

  @Override public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LogicOperator getOperator() {
    return operator;
  }

  public void setOperator(LogicOperator operator) {
    this.operator = operator;
  }

  @Override public int getTimeout() {
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

  public List<StateDefinition> getStates() {
    return states;
  }

  public void setStates(List<StateDefinition> states) {
    this.states = states;
  }

  @Override public List<String> getActions() {
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
