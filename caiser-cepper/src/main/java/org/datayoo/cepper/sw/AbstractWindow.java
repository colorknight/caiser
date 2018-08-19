package org.datayoo.cepper.sw;

import org.apache.commons.lang.Validate;
import org.datayoo.cepper.CepperListener;
import org.datayoo.cepper.SlideWindow;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.DataSetMapImpl;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.Selector;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangtadin on 17/1/25.
 */
public abstract class AbstractWindow implements SlideWindow {

  protected String eventStreamName;

  protected CepperMetadata metadata;

  protected DataSetMap dataSetMap = new DataSetMapImpl();

  protected List<CepperListener> cepperListeners = new LinkedList<CepperListener>();

  protected LinkedList<List> buckets = new LinkedList<List>();

  protected int bucketCount = 0;

  protected List curBucket;

  protected Selector selector;

  public AbstractWindow(String eventStreamName, CepperMetadata metadata, Selector selector) {
    this.eventStreamName = eventStreamName;
    this.metadata = metadata;
    this.selector = selector;
    bucketCount = calcBucketCount(metadata);
    curBucket = new LinkedList();
    buckets.add(curBucket);
  }

  protected int calcBucketCount(CepperMetadata metadata) {
    if (metadata.getBucketSize() == 0)
      return metadata.getCapacity();
    Validate.isTrue(metadata.getCapacity()%metadata.getBucketSize() == 0,
        "capacity should be divided with no remainder by bucketSize!");
    int bucketCount = metadata.getCapacity()/metadata.getBucketSize();
    return bucketCount;
  }

  @Override public void setContextDataSet(DataSetMap dataSetMap) {
    if (dataSetMap == null)
      return;
    this.dataSetMap = new DataSetMapImpl(dataSetMap);
  }

  @Override public void addCepperListener(CepperListener cepListener) {
    synchronized (cepperListeners) {
      cepperListeners.add(cepListener);
    }
  }

  @Override public void removeCepperListener(CepperListener cepListener) {
    synchronized (cepListener) {
      cepperListeners.remove(cepListener);
    }
  }

  protected void nofityCepListeners(RecordSet recordSet, List orginalData) {
    for(CepperListener listener : cepperListeners) {
      listener.onRecordSet(recordSet, orginalData);
    }
  }

  protected void operate() {
    if (buckets.size() < bucketCount)
      return;
    List list = packBuckets();
    dataSetMap.putDataSet(eventStreamName, list);
    selector.select(dataSetMap);
    try {
      RecordSet recordSet = selector.getRecordSet();
      if (recordSet.getRecords().size() > 0)
        nofityCepListeners(recordSet, list);
    } finally {
      selector.clear();
    }
  }

  protected List packBuckets() {
    List list = new LinkedList();
    for(List bucket : buckets) {
      list.addAll(bucket);
    }
    return list;
  }

  protected void updateBuckets() {
    if (buckets.size() == bucketCount)
      buckets.removeFirst();
    curBucket = new LinkedList();
    buckets.add(curBucket);
  }

  @Override public synchronized void clear() {
    buckets = new LinkedList();
    curBucket = new LinkedList();
    buckets.add(curBucket);
  }
}
