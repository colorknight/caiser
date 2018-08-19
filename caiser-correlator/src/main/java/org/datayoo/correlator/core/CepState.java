package org.datayoo.correlator.core;

import org.datayoo.cepper.CepperListener;
import org.datayoo.cepper.core.MoqlCepper;
import org.datayoo.correlator.CorrelatorConstants;
import org.datayoo.correlator.SceneInstanceContext;
import org.datayoo.correlator.metadata.StateMetadata;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.DataSetMapImpl;
import org.datayoo.moql.RecordSet;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class CepState extends AbstractState<StateMetadata> {

  protected MoqlCepper cepper;

  public CepState(StateMetadata metadata, SceneInstanceContext context) {
    super(metadata, context);
    cepper = new MoqlCepper(CorrelatorConstants.DATA_STREAM_NAME, metadata);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet(CorrelatorConstants.RK_GCTX, context.getEnv());
    dataSetMap.putDataSet(CorrelatorConstants.RK_CTX, context);
    cepper.setContextDataSet(dataSetMap);
    cepper.addCepperListener(new InnerCepperListener());
  }

  public MoqlCepper getCepper() {
    return cepper;
  }

  @Override protected void innerReset() {
    context.clear();
    cepper.clear();
  }

  protected void innerTransit(Object entity) {
    cepper.operate(entity);
  }

  @Override public void onTick() {
    try {
      cepper.onTick();
    } finally {
      super.onTick();
    }
  }

  protected class InnerCepperListener implements CepperListener {

    @Override public void onRecordSet(RecordSet recordSet, List originalData) {
      onStateTransition(true, recordSet, originalData);
    }
  }
}
