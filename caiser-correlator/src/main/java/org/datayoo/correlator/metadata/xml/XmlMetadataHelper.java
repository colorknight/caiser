/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.correlator.metadata.xml;

import org.apache.commons.lang3.Validate;
import org.datayoo.cepper.sw.SlideWindowEnum;
import org.datayoo.correlator.metadata.*;
import org.datayoo.moql.MapEntry;
import org.datayoo.moql.MapEntryImpl;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.util.StringFormater;
import org.datayoo.moql.xml.XmlAccessException;
import org.datayoo.moql.xml.XmlElementFormater;
import org.dom4j.*;

import java.util.*;

/**
 * @author Tang Tadin
 */
class XmlMetadataHelper {

  public static final String SCENES_ELEMENT = "scenes";

  public static final String SCENE_ELEMENT = "scene";

  public static final String STATE_ELEMENT = "state";

  public static final String LOGIC_STATE_ELEMENT = "logicstate";

  public static final String ACTION_ELEMENT = "action";

  public static final String EVENT_ELEMENT = "event";

  public static final String FIELD_ELEMENT = "field";

  public static final String MOQL_ELEMENT = "moql";

  public static final String WIN_TYPE_ELEMENT = "wintype";

  public static final String BUCKET_COUNT_ELEMENT = "bucketcount";

  public static final String BUCKET_SIZE_ELEMENT = "bucketsize";

  public static final String BUCKET_DURATION_ELEMENT = "bucketduration";

  public static final String MAX_INSTANCES_ATTRIBUTE = "maxInstances";

  public static final String DEAD_LOCK_TIMEOUT_ATTRIBUTE = "deadLockTimeout";

  public static final String NAME_ATTRIBUTE = "name";

  public static final String TIMEOUT_ATTRIBUTE = "timeout";

  public static final String NEGATIVE_ATTRIBUTE = "negative";

  public static final String OPERATOR_ATTRIBUTE = "operator";

  protected Map<String, XmlElementFormater<Object>> extendedElementFormaters = new HashMap<String, XmlElementFormater<Object>>();

  public List<SceneMetadata> readSceneMetadatas(Element element)
      throws XmlAccessException {
    Validate.notNull(element, "Parameter 'element' is null!");
    if (!element.getName().equals(SCENES_ELEMENT)) {
      throw new IllegalArgumentException(
          StringFormater.format("Invalid element '{}'!", element.getName()));
    }
    List<SceneMetadata> sceneMetadatas = new LinkedList<SceneMetadata>();
    for (Iterator<Element> it = element.elementIterator(SCENE_ELEMENT); it
        .hasNext(); ) {
      Element el = it.next();
      sceneMetadatas.add(readSceneMetadata(el));
    }
    return sceneMetadatas;
  }

  protected String getAttribute(Element element, String attribute,
      boolean option) throws XmlAccessException {
    Attribute attr = (Attribute) element.attribute(attribute);
    if (attr != null) {
      return attr.getValue();
    }
    if (option)
      return null;
    throw new XmlAccessException(StringFormater
        .format("There is no attribute '{}' in element '{}'!", attribute,
            element.getName()));
  }

  protected String getElementText(Element element, String textElement,
      boolean option) throws XmlAccessException {
    Element el = (Element) element.element(textElement);
    if (el != null) {
      return el.getTextTrim();
    }
    if (option)
      return null;
    throw new XmlAccessException(StringFormater
        .format("There is no element '{}' in element '{}'!", textElement,
            element.getName()));
  }

  @SuppressWarnings({ "rawtypes"
  }) protected SceneMetadata readSceneMetadata(Element element)
      throws XmlAccessException {
    SceneMetadata sceneMetadata = createSceneMetadata(element);
    for (Iterator it = element.elementIterator(); it.hasNext(); ) {
      Element el = (Element) it.next();
      if (el.getName().equals(STATE_ELEMENT)) {
        StateMetadata stateMetadata = readStateMetadata(el);
        sceneMetadata.getStates().add(stateMetadata);
      } else if (el.getName().equals(LOGIC_STATE_ELEMENT)) {
        LogicStateMetadata stateMetadata = readLogicStateMetadata(el);
        sceneMetadata.getStates().add(stateMetadata);
      }
    }
    return sceneMetadata;
  }

  protected SceneMetadata createSceneMetadata(Element element)
      throws XmlAccessException {
    SceneMetadata sceneMetadata = new SceneMetadata();
    String value = getAttribute(element, NAME_ATTRIBUTE, false);
    sceneMetadata.setName(value);
    value = getAttribute(element, MAX_INSTANCES_ATTRIBUTE, false);
    sceneMetadata.setMaxInstances(Integer.valueOf(value));
    value = getAttribute(element, DEAD_LOCK_TIMEOUT_ATTRIBUTE, true);
    if (value != null) {
      sceneMetadata.setDeadlockTimeout(Integer.valueOf(value));
    }
    return sceneMetadata;
  }

  protected StateMetadata readStateMetadata(Element element)
      throws XmlAccessException {
    StateMetadata stateMetadata = createStateMetadata(element);
    for (Iterator it = element.elementIterator(); it.hasNext(); ) {
      Element el = (Element) it.next();
      if (el.getName().equals(ACTION_ELEMENT)) {
        String value = el.getTextTrim();
        stateMetadata.getActions().add(value);
      } else if (el.getName().equals(EVENT_ELEMENT)) {
        EventMetadata eventMetadata = readEventMetadata(el);
        stateMetadata.setEventMetadata(eventMetadata);
      }
    }
    return stateMetadata;
  }

  protected StateMetadata createStateMetadata(Element element)
      throws XmlAccessException {
    StateMetadata stateMetadata = new StateMetadata();
    String value = getAttribute(element, NAME_ATTRIBUTE, false);
    stateMetadata.setName(value);
    value = getAttribute(element, TIMEOUT_ATTRIBUTE, false);
    stateMetadata.setTimeout(Integer.valueOf(value));
    value = getAttribute(element, NEGATIVE_ATTRIBUTE, false);
    stateMetadata.setNegative(Boolean.valueOf(value));

    value = getElementText(element, MOQL_ELEMENT, false);
    stateMetadata.setMoql(value);
    value = getElementText(element, WIN_TYPE_ELEMENT, false);
    stateMetadata.setWinType(SlideWindowEnum.valueOf(value));
    value = getElementText(element, BUCKET_COUNT_ELEMENT, false);
    stateMetadata.setBucketCount(Integer.valueOf(value));
    value = getElementText(element, BUCKET_SIZE_ELEMENT, true);
    if (value != null)
      stateMetadata.setBucketSize(Integer.valueOf(value));
    value = getElementText(element, BUCKET_DURATION_ELEMENT, true);
    if (value != null)
      stateMetadata.setBucketDuration(Integer.valueOf(value));
    return stateMetadata;
  }

  protected EventMetadata readEventMetadata(Element element)
      throws XmlAccessException {
    EventMetadata eventMedata = new EventMetadata();
    for (Iterator it = element.elementIterator(FIELD_ELEMENT); it.hasNext(); ) {
      Element el = (Element) it.next();
      String name = getAttribute(el, NAME_ATTRIBUTE, false);
      String value = el.getTextTrim();
      MapEntry<String, String> entry = new MapEntryImpl<String, String>(name);
      entry.setValue(value);
      eventMedata.getEntries().add(entry);
    }
    return eventMedata;
  }

  protected LogicStateMetadata readLogicStateMetadata(Element element)
      throws XmlAccessException {
    LogicStateMetadata logicStateMetadata = createLogicStateMetadata(element);
    for (Iterator it = element.elementIterator(); it.hasNext(); ) {
      Element el = (Element) it.next();
      if (el.getName().equals(STATE_ELEMENT)) {
        StateMetadata stateMetadata = readStateMetadata(el);
        logicStateMetadata.getStates().add(stateMetadata);
      } else if (el.getName().equals(ACTION_ELEMENT)) {
        String value = el.getTextTrim();
        logicStateMetadata.getActions().add(value);
      } else if (el.getName().equals(EVENT_ELEMENT)) {
        EventMetadata eventMetadata = readEventMetadata(el);
        logicStateMetadata.setEventMetadata(eventMetadata);
      }
    }
    return logicStateMetadata;
  }

  protected LogicStateMetadata createLogicStateMetadata(Element element)
      throws XmlAccessException {
    LogicStateMetadata stateMetadata = new LogicStateMetadata();
    String value = getAttribute(element, NAME_ATTRIBUTE, false);
    stateMetadata.setName(value);
    value = getAttribute(element, OPERATOR_ATTRIBUTE, false);
    stateMetadata.setOperator(LogicOperator.valueOf(value));
    value = getAttribute(element, TIMEOUT_ATTRIBUTE, false);
    stateMetadata.setTimeout(Integer.valueOf(value));
    value = getAttribute(element, NEGATIVE_ATTRIBUTE, false);
    stateMetadata.setNegative(Boolean.valueOf(value));
    return stateMetadata;
  }

  public Element writeSceneMetadatas(Element element,
      List<SceneMetadata> sceneMetadatas) throws XmlAccessException {
    Validate.notNull(sceneMetadatas, "Parameter 'sceneMetadatas' is null!");
    Element scenesElement = createElement(element, SCENES_ELEMENT);
    for (SceneMetadata sceneMetadata : sceneMetadatas) {
      writeSceneMetadata(scenesElement, sceneMetadata);
    }
    return scenesElement;
  }

  protected Element createElement(Element element, String rootName) {
    Element elRoot = null;
    if (element != null) {
      elRoot = element.addElement(rootName);
    } else {
      Document doc = DocumentHelper.createDocument();
      elRoot = doc.addElement(rootName);
    }
    return elRoot;
  }

  protected void writeSceneMetadata(Element element,
      SceneMetadata sceneMetadata) throws XmlAccessException {
    Element elScene = element.addElement(SCENE_ELEMENT);
    elScene.addAttribute(NAME_ATTRIBUTE, sceneMetadata.getName());
    elScene.addAttribute(MAX_INSTANCES_ATTRIBUTE,
        String.valueOf(sceneMetadata.getMaxInstances()));
    elScene.addAttribute(DEAD_LOCK_TIMEOUT_ATTRIBUTE,
        String.valueOf(sceneMetadata.getDeadlockTimeout()));
    for (StateDefinition stateDefinition : sceneMetadata.getStates()) {
      if (stateDefinition instanceof StateMetadata) {
        writeStateMetadata(elScene, (StateMetadata) stateDefinition);
      } else {
        writeLogicStateMetadata(elScene, (LogicStateMetadata) stateDefinition);
      }
    }
  }

  protected void writeStateMetadata(Element element,
      StateMetadata stateMetadata) {
    Element elState = element.addElement(STATE_ELEMENT);
    elState.addAttribute(NAME_ATTRIBUTE, stateMetadata.getName());
    elState.addAttribute(TIMEOUT_ATTRIBUTE,
        String.valueOf(stateMetadata.getTimeout()));
    elState.addAttribute(NEGATIVE_ATTRIBUTE,
        String.valueOf(stateMetadata.isNegative()));

    Element el = elState.addElement(MOQL_ELEMENT);
    el.setText(stateMetadata.getMoql());
    el = elState.addElement(WIN_TYPE_ELEMENT);
    el.setText(stateMetadata.getWinType().name());
    el = elState.addElement(BUCKET_COUNT_ELEMENT);
    el.setText(String.valueOf(stateMetadata.getBucketCount()));
    if (stateMetadata.getBucketSize() > 0) {
      el = elState.addElement(BUCKET_SIZE_ELEMENT);
      el.setText(String.valueOf(stateMetadata.getBucketSize()));
    }
    if (stateMetadata.getBucketDuration() > 0) {
      el = elState.addElement(BUCKET_DURATION_ELEMENT);
      el.setText(String.valueOf(stateMetadata.getBucketDuration()));
    }
    writeActions(elState, stateMetadata.getActions());
    writeEvent(elState, stateMetadata.getEventMetadata());
  }

  protected void writeActions(Element element, List<String> actions) {
    for (String action : actions) {
      Element el = element.addElement(ACTION_ELEMENT);
      el.setText(action);
    }
  }

  protected void writeEvent(Element element, EventMetadata eventMetadata) {
    Element elEvent = element.addElement(EVENT_ELEMENT);
    for (MapEntry<String, String> entry : eventMetadata.getEntries()) {
      Element elField = elEvent.addElement(FIELD_ELEMENT);
      elField.addAttribute(NAME_ATTRIBUTE, entry.getKey());
      elField.setText(entry.getValue());
    }
  }

  protected void writeLogicStateMetadata(Element element,
      LogicStateMetadata stateMetadata) {
    Element elState = element.addElement(LOGIC_STATE_ELEMENT);
    elState.addAttribute(NAME_ATTRIBUTE, stateMetadata.getName());
    elState.addAttribute(TIMEOUT_ATTRIBUTE,
        String.valueOf(stateMetadata.getTimeout()));
    elState.addAttribute(NEGATIVE_ATTRIBUTE,
        String.valueOf(stateMetadata.isNegative()));
    elState
        .addAttribute(OPERATOR_ATTRIBUTE, stateMetadata.getOperator().name());

    for (StateDefinition stateDefinition : stateMetadata.getStates()) {
      if (stateDefinition instanceof StateMetadata) {
        writeStateMetadata(elState, (StateMetadata) stateDefinition);
      } else {
        writeLogicStateMetadata(elState, (LogicStateMetadata) stateDefinition);
      }
    }
    writeActions(elState, stateMetadata.getActions());
    writeEvent(elState, stateMetadata.getEventMetadata());
  }

}
