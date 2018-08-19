package org.datayoo.cepper;


import org.datayoo.moql.DataSetMap;

import java.util.List;

/**
 * Created by tangtadin on 17/1/22.
 */
public interface SlideWindow extends Ticker {

  void setContextDataSet(DataSetMap dataSetMap);

  void push(List dataSet);

  void push(Object entity);

  void clear();

  void addCepperListener(CepperListener listener);

  void removeCepperListener(CepperListener listener);
}
