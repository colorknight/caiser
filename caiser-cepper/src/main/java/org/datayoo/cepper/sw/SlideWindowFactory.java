package org.datayoo.cepper.sw;

import org.datayoo.cepper.SlideWindow;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.moql.Selector;

/**
 * Created by tangtadin on 17/1/26.
 */
public class SlideWindowFactory<T> {

  public SlideWindow createSlideWindow(SlideWindowEnum type,
      String eventStreamName, CepperMetadata metadata, Selector selector) {
    if (type == SlideWindowEnum.SW_BATCH)
      return new BatchWindow(eventStreamName, metadata, selector);
    else if (type == SlideWindowEnum.SW_TIME)
      return new TimeWindow(eventStreamName, metadata, selector);
    else if (type == SlideWindowEnum.SW_BATCH_TIME)
      return new BatchAndTimeWindow(eventStreamName, metadata, selector);
    else if (type == SlideWindowEnum.SW_MATCHER)
      return new MatcherWindow(eventStreamName, metadata, selector);
    else
      throw new IllegalArgumentException("");
  }
}
