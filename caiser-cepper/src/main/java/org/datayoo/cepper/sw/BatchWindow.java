package org.datayoo.cepper.sw;

import org.apache.commons.lang3.Validate;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.Selector;

import java.util.List;

/**
 * Created by tangtadin on 17/1/24.
 */
public class BatchWindow extends AbstractWindow {

  public BatchWindow(String eventStreamName, CepperMetadata metadata, Selector selector) {
    super(eventStreamName, metadata, selector);
    Validate.isTrue(metadata.getBucketSize() > 0, "bucket size less than 1!");
  }

  @Override synchronized public void push(List dataSet) {
    for(Object entity : dataSet) {
      push(entity);
    }
  }

  @Override synchronized public void push(Object entity) {
    curBucket.add(entity);
    if (curBucket.size() == metadata.getBucketSize()) {
      operate();
      updateBuckets();
    }
  }

  @Override public void onTick() {

  }
}
