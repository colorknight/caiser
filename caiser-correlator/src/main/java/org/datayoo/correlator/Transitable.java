package org.datayoo.correlator;

import org.datayoo.cepper.Ticker;

import java.util.List;

public interface Transitable extends Ticker {

  void transit(List dataSet);

  void transit(Object entity);

  void setCorrelatorEventListener(CorrelatorEventListener listener);

}
