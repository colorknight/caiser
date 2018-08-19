package org.datayoo.correlator.core;

import java.util.*;

/**
 * Created by tangtadin on 17/1/27.
 */
public abstract class Test1DataSimulator {

  public static List<Map<String, Object>> createState1DataList(int dest) {
    List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();
    Map<String, Object> evt = new HashMap<String, Object>();
    evt.put("category", "conn");
    evt.put("sip", "192.168.1.1");
    evt.put("dip", "192.168.2." + dest);
    dataList.add(evt);
    return dataList;
  }

  public static List<Map<String, Object>> createState21DataList(int count,
      int times) {
    List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();
    for (int i = 0; i < times; i++) {
      for (int j = 0; j < count; j++) {
        Map<String, Object> evt = new HashMap<String, Object>();
        evt.put("category", "conn1");
        evt.put("sip", "192.168.1." + (j + 1));
        evt.put("dip", "192.168.2." + (i + 1));
        dataList.add(evt);
      }
    }
    return dataList;
  }
}
