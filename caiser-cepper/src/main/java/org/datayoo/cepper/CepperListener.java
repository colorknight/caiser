package org.datayoo.cepper;


import org.datayoo.moql.RecordSet;

import java.util.List;

/**
 * Created by tangtadin on 17/1/18.
 */
public interface CepperListener {
  /**
   * @param recordSet    the result of the cepper
   * @param originalData the original data which the cepper get the result
   */
  void onRecordSet(RecordSet recordSet, List originalData);
}
