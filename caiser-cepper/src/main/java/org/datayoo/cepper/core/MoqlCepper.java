package org.datayoo.cepper.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.cepper.Cepper;
import org.datayoo.cepper.CepperListener;
import org.datayoo.cepper.SlideWindow;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.cepper.sw.SlideWindowFactory;
import org.datayoo.moql.*;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.metadata.QueryableMetadata;
import org.datayoo.moql.metadata.SelectorMetadata;
import org.datayoo.moql.metadata.TableMetadata;
import org.datayoo.moql.metadata.TablesMetadata;
import org.datayoo.moql.parser.MoqlParser;

import java.util.List;

/**
 * Created by tangtadin on 17/1/26.
 */
public class MoqlCepper implements Cepper {

  protected EntityMap entityMap = new EntityMapImpl();

  protected Filter filter;

  protected String eventStreamAlias;

  protected SlideWindow slideWindow;

  public MoqlCepper(String eventStreamName, CepperMetadata metadata) {
    Validate.notEmpty(eventStreamName, "eventStreamName is empty!");
    Validate.notNull(metadata, "metadata is null!");

    Selector selector = buildFilterAndSelector(eventStreamName, metadata.getMoql());
    SlideWindowFactory factory = new SlideWindowFactory();
    slideWindow = factory
        .createSlideWindow(metadata.getWinType(), eventStreamName, metadata, selector);
  }

  protected Selector buildFilterAndSelector(String eventStreamName, String moql) {
    SelectorMetadata selectorMetadata = parseMoql(moql);
    try {
      if (selectorMetadata.getWhere() == null) {
        return MoqlEngine.createSelector(selectorMetadata);
      }
      filter = MoqlEngine.createFilter(selectorMetadata.getWhere());
      getEventStreamAlias(eventStreamName, selectorMetadata.getTables());
      selectorMetadata.setWhere(null);
      return MoqlEngine.createSelector(selectorMetadata);
    } catch (MoqlException e) {
      throw new IllegalArgumentException("Invalid moql!");
    }
  }

  protected SelectorMetadata parseMoql(String moql) {
    try {
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(moql);
      return (SelectorMetadata) selectorDefinition;
    } catch (MoqlException e) {
      throw new IllegalArgumentException("Invalid moql!");
    }
  }

  protected void getEventStreamAlias(String eventStreamName, TablesMetadata tablesMetadata) {
    List<QueryableMetadata> queryableMetadatas = tablesMetadata.getTables();
    for(QueryableMetadata metadata : queryableMetadatas) {
      if (metadata instanceof TableMetadata) {
        TableMetadata tableMetadata = (TableMetadata)metadata;
        if (tableMetadata.getValue().equals(eventStreamName)) {
          eventStreamAlias = tableMetadata.getName();
          return;
        }
      }
    }
  }

  @Override public void setContextDataSet(DataSetMap dataSetMap) {
    if (dataSetMap == null)
      return;
    entityMap = new EntityMapImpl();
    for(MapEntry<String, Object> entry : dataSetMap.entrySet()) {
      entityMap.putEntity(entry.getKey(), entry.getValue());
    }
    slideWindow.setContextDataSet(dataSetMap);
  }

  @Override public void addCepperListener(CepperListener listener) {
    slideWindow.addCepperListener(listener);
  }

  @Override public void removeCepperListener(CepperListener listener) {
    slideWindow.removeCepperListener(listener);
  }

  @Override public void operate(List dataSet) {
    if (filter == null) {
      slideWindow.push(dataSet);
    } else {
      for(Object entity : dataSet) {
        operate(entity);
      }
    }
  }

  @Override public synchronized void operate(Object entity) {
    if (entity == null)
      return;
    if (filter == null)
      slideWindow.push(entity);
    else {
      entityMap.putEntity(this.eventStreamAlias, entity);
      if (filter.isMatch(entityMap))
        slideWindow.push(entity);
    }
  }

  @Override public void clear() {
    slideWindow.clear();
  }

  @Override public void onTick() {
    slideWindow.onTick();
  }
}
