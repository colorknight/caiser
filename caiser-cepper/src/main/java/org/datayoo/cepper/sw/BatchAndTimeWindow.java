package org.datayoo.cepper.sw;

import org.apache.commons.lang.Validate;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.Selector;

import java.util.List;

/**
 * Created by tangtadin on 17/1/26.
 */
public class BatchAndTimeWindow extends AbstractWindow {

  protected long curBucketMills = 0;

  protected long bucketMills = 0;

  public BatchAndTimeWindow(String eventStreamName, CepperMetadata metadata,
      Selector selector) {
    super(eventStreamName, metadata, selector);
    Validate.isTrue(metadata.getBucketSize() > 0, "bucket size less than 1!");
    Validate.isTrue(metadata.getBucketDuration() > 0,
        "bucket duration less than 1!");
    bucketMills = metadata.getBucketDuration() * 1000;
    curBucketMills = System.currentTimeMillis();
  }

  @Override public synchronized void push(List dataSet) {
    for (Object entity : dataSet) {
      push(entity);
    }
  }

  @Override public synchronized void push(Object entity) {
    curBucket.add(entity);
    if (curBucket.size() == metadata.getBucketSize()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }

  @Override public synchronized void onTick() {
    if (curBucketMills + bucketMills > System.currentTimeMillis()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }
}
