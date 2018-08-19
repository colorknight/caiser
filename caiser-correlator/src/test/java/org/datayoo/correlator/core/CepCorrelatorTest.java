package org.datayoo.correlator.core;

import org.datayoo.correlator.CorrelatorEventListener;
import org.datayoo.correlator.action.SetProperty;
import org.datayoo.correlator.metadata.SceneMetadata;
import org.datayoo.correlator.metadata.xml.SceneFormater;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.xml.DefaultDocumentFormater;
import org.datayoo.moql.xml.XmlAccessException;

import java.util.List;
import java.util.Map;

public class CepCorrelatorTest {

  public static void main(String[] args) {
    DefaultDocumentFormater<List<SceneMetadata>> documentFormater = new DefaultDocumentFormater<List<SceneMetadata>>();
    SceneFormater sceneFormater = new SceneFormater();
    documentFormater.setFormater(sceneFormater);
    try {
      List<SceneMetadata> scenes = documentFormater.importObjectFromFile(
          "file:./caiser-correlator/resources/test_m_scenes.xml");
      CorrelatorEventListener listener = new CorrelatorEventPrintListener();
      CepCorrelator correlator = new CepCorrelator(scenes);
      correlator.addCorrelatorEventListener(listener);
      testSingleInstance(correlator);
      testMultiInstances(correlator);
      testMultiInstances(correlator);
      correlator.shutdown();
      System.out.println("success");
    } catch (XmlAccessException e) {
      e.printStackTrace();
    }
  }

  protected static void testSingleInstance(CepCorrelator correlator) {
    List<Map<String, Object>> dataList = Test1DataSimulator
        .createState1DataList(1);
    correlator.transit(dataList);
    dataList = Test1DataSimulator.createState21DataList(10, 1);
    correlator.transit(dataList);
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected static void testMultiInstances(CepCorrelator correlator) {
    List<Map<String, Object>> dataList = Test1DataSimulator
        .createState1DataList(1);
    correlator.transit(dataList);
    dataList = Test1DataSimulator
        .createState1DataList(2);
    correlator.transit(dataList);
    dataList = Test1DataSimulator.createState21DataList(10, 2);
    correlator.transit(dataList);
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
