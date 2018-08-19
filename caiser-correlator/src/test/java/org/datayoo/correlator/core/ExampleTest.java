package org.datayoo.correlator.core;

import junit.framework.TestCase;
import org.datayoo.correlator.CorrelatorEventListener;
import org.datayoo.correlator.metadata.SceneMetadata;
import org.datayoo.correlator.metadata.xml.SceneFormater;
import org.datayoo.moql.xml.DefaultDocumentFormater;
import org.datayoo.moql.xml.XmlAccessException;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ExampleTest extends TestCase {

  @Test
  public void testPortScan() {
    DefaultDocumentFormater<List<SceneMetadata>> documentFormater = new DefaultDocumentFormater<List<SceneMetadata>>();
    SceneFormater sceneFormater = new SceneFormater();
    documentFormater.setFormater(sceneFormater);
    try {
      List<SceneMetadata> scenes = documentFormater.importObjectFromFile(
          "file:./resources/examples.xml");
      CorrelatorEventListener listener = new CorrelatorEventPrintListener();
      CepCorrelator correlator = new CepCorrelator(scenes);
      correlator.addCorrelatorEventListener(listener);
      List<Map<String, Object>> dataList = ExampleDataSimulator
          .createPortScanData(3000);
      correlator.transit(dataList);
      correlator.shutdown();
      System.out.println("success");
    } catch (XmlAccessException e) {
      e.printStackTrace();
    }
  }
}
