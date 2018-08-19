package org.datayoo.correlator.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ExampleDataSimulator {

  public static List<Map<String, Object>> createPortScanData(int size) {
    List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();
    for(int i = 0; i < size; i++) {
      Map<String, Object> evt = new HashMap<String, Object>();
      evt.put("SRC_ADDRESS", "192.168.1.1");
      evt.put("DEST_ADDRESS", "128.12.3."+i%250);
      evt.put("DEST_PORT", 80);
      dataList.add(evt);
    }
    return dataList;
  }
}
