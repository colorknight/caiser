package org.datayoo.cepper.sw;

import org.apache.commons.lang3.Validate;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.*;
import org.datayoo.moql.engine.MoqlEngine;

import java.util.List;
import java.util.Objects;

/**
 * Created by tangtadin on 17/1/25.
 */
public class MatcherWindow extends AbstractWindow {

  public static final String PARAM_MATCHER_EXPRESSION = "win.matcher.expression";

  protected Operand operand;

  protected Object curValue;

  protected EntityMap entityMap = new EntityMapImpl();

  public MatcherWindow(String eventStreamName, CepperMetadata metadata, Selector selector) {
    super(eventStreamName, metadata, selector);
    String expression = metadata.getParameters().get(PARAM_MATCHER_EXPRESSION);
    Validate.notEmpty(expression, "matcher expression is empty!");
    try {
      operand = MoqlEngine.createOperand(expression);
    } catch (MoqlException e) {
      throw new IllegalArgumentException("matcher expression is invalid!");
    }
  }

  @Override public synchronized void push(List dataSet) {
    for(Object entity : dataSet) {
      push(entity);
    }
  }

  @Override public synchronized void push(Object entity) {
    entityMap.putEntity(eventStreamName, entity);
    Object value = operand.operate(entityMap);
    if (!Objects.deepEquals(curValue, value)) {
      if (curBucket.size() != 0) {
        operate();
        updateBuckets();
      }
      curValue = value;
    }
    curBucket.add(entity);
  }

  @Override public void onTick() {

  }
}
