package org.datayoo.correlator.action;

import org.datayoo.correlator.CorrelatorConstants;
import org.datayoo.correlator.SceneInstanceContext;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.constant.Constant;
import org.datayoo.moql.operand.constant.StringConstant;
import org.datayoo.moql.operand.function.AbstractFunction;

import java.util.List;

public class SetProperty extends AbstractFunction {

  public static final String FUNCTION_NAME = "setProperty";

  protected String key;

  public SetProperty(List<Operand> parameters) {
    super(FUNCTION_NAME, 2, parameters);
    if (!(parameters.get(0) instanceof StringConstant)) {
      throw new IllegalArgumentException("The key isn't a stirng!");
    }
    key = (String) parameters.get(0).operate(null);
  }

  @Override protected Object innerOperate(EntityMap entityMap) {
    Object value = parameters.get(1).operate(entityMap);
    SceneInstanceContext context = (SceneInstanceContext) entityMap
        .getEntity(CorrelatorConstants.RK_CTX);
    context.put(key, value);
    return value;
  }
}
