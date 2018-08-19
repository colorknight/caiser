package org.datayoo.correlator.core;

import org.datayoo.correlator.CorrelatorEvent;
import org.datayoo.correlator.CorrelatorEventListener;

import java.util.Map;

public class CorrelatorEventPrintListener implements CorrelatorEventListener {
  @Override public void onEvent(CorrelatorEvent event) {
    for(Map.Entry<String, Object> entry : event.entrySet()) {
      System.out.println(entry.getKey()+"="+entry.getValue().toString());
    }
    System.out.println("-----------------------------");
  }
}
