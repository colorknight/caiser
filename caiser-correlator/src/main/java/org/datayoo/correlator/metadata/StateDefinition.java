package org.datayoo.correlator.metadata;

import java.io.Serializable;
import java.util.List;

public interface StateDefinition extends Serializable {

  String getName();

  int getTimeout();

  boolean isNegative();

  List<String> getActions();

  EventMetadata getEventMetadata();

}
