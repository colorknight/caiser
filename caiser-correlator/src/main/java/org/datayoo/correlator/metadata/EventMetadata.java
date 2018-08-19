package org.datayoo.correlator.metadata;

import org.datayoo.moql.MapEntry;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class EventMetadata implements Serializable {

  protected List<MapEntry<String, String>> entries = new LinkedList<MapEntry<String, String>>();

  public List<MapEntry<String, String>> getEntries() {
    return entries;
  }

  public void setEntries(List<MapEntry<String, String>> entries) {
    this.entries = entries;
  }
}
