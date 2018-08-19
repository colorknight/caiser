package org.datayoo.correlator.metadata.xml;

import junit.framework.TestCase;
import org.datayoo.correlator.metadata.SceneMetadata;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.xml.DefaultDocumentFormater;
import org.datayoo.moql.xml.XmlAccessException;

import java.util.List;

public class SceneFormaterTest {

  public static void main(String[] args) {
    DefaultDocumentFormater<List<SceneMetadata>> documentFormater = new DefaultDocumentFormater<List<SceneMetadata>>();
    SceneFormater sceneFormater = new SceneFormater();
    documentFormater.setFormater(sceneFormater);
    try {
      List<SceneMetadata> scenes = documentFormater.importObjectFromFile(
          "file:./caiser-correlator/resources/test_scenes.xml");
      System.out.println(scenes.size());
      documentFormater.exportObjectToFile(
          "file:./caiser-correlator/resources/test_scenesw.xml", scenes);
      scenes = documentFormater.importObjectFromFile(
          "file:./caiser-correlator/resources/test_scenesw.xml");
      System.out.println(scenes.size());
    } catch (XmlAccessException e) {
      e.printStackTrace();
    }
  }

}
