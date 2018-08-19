package org.datayoo.cepper;


import org.datayoo.moql.DataSetMap;

import java.util.List;

/**
 * Created by tangtadin on 17/1/18.
 */
public interface Cepper extends Ticker {

  void setContextDataSet(DataSetMap dataSetMap);

  void addCepperListener(CepperListener listener);

  void removeCepperListener(CepperListener listener);

  void operate(List dataSet);

  void operate(Object entity);

  void clear();
}
