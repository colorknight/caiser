package org.datayoo.correlator.core;

import org.apache.commons.lang.Validate;
import org.datayoo.correlator.CorrelatorEvent;
import org.datayoo.correlator.EventCreator;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.MapEntry;
import org.datayoo.moql.MapEntryImpl;
import org.datayoo.moql.Operand;

import java.util.LinkedList;
import java.util.List;

public class GeneralEventCreator implements EventCreator {

  protected List<MapEntry<String, Operand>> entries = new LinkedList<MapEntry<String, Operand>>();

  @Override public CorrelatorEvent create(EntityMap entityMap) {
    CorrelatorEvent event = new GeneralCorrelatorEvent();
    for (MapEntry<String, Operand> entry : entries) {
      event.put(entry.getKey(), entry.getValue().operate(entityMap));
    }
    return event;
  }

  public void addEntry(String key, Operand value) {
    MapEntry<String, Operand> entry = new MapEntryImpl<String, Operand>(key);
    entry.setValue(value);
    entries.add(entry);
  }

}
