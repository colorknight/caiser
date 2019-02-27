package org.datayoo.cepper.sw;

import org.apache.commons.lang3.Validate;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.Selector;

import java.util.List;

/**
 * Created by tangtadin on 17/1/24.
 */
public class TimeWindow extends AbstractWindow {

  protected long curBucketMills = 0;

  protected long bucketMills = 0;

  public TimeWindow(String eventStreamName, CepperMetadata metadata,
      Selector selector) {
    super(eventStreamName, metadata, selector);
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
  }

  @Override public synchronized void onTick() {
    if (curBucketMills + bucketMills < System.currentTimeMillis()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }
}
