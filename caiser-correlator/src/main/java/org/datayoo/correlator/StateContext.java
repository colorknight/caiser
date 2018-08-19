package org.datayoo.correlator;


import org.datayoo.moql.RecordSet;

import java.util.List;

public interface StateContext {

  String getName();

  StateStatus getStateStatus();

  RecordSet getRecordSet();

  List getOriginalData();

  void clear();
}
