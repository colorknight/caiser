package org.datayoo.cepper;

import junit.framework.TestCase;
import org.datayoo.cepper.core.CepperPrintListener;
import org.datayoo.cepper.core.MoqlCepper;
import org.datayoo.cepper.metadata.CepperMetadata;
import org.datayoo.cepper.sw.MatcherWindow;
import org.datayoo.cepper.sw.SlideWindowEnum;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.DataSetMapImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangtadin on 17/1/27.
 */
public class TestMoqlCepper extends TestCase {


  public void testBatchWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    CepperMetadata metadata = new CepperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_BATCH);
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(5);
    metadata.setBucketSize(10);
    Cepper cepper = new MoqlCepper("evt", metadata);
    cepper.addCepperListener(new CepperPrintListener());
    for(Object[] data : dataList) {
      cepper.operate(data);
    }
  }

  public void testBatchWindow2() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    CepperMetadata metadata = new CepperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_BATCH);
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt where evt[0] > ctx.name group by name having sum > 100");
    metadata.setBucketCount(5);
    metadata.setBucketSize(10);
    Map<String, Object> ctx = new HashMap<String, Object>();
    ctx.put("name", "A");
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("ctx", ctx);
    Cepper cepper = new MoqlCepper("evt", metadata);
    cepper.setContextDataSet(dataSetMap);
    cepper.addCepperListener(new CepperPrintListener());
    for(Object[] data : dataList) {
      cepper.operate(data);
    }
  }

  public void testMatcherWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    dataList.add(new Object[] {'1', 0, 1});
    CepperMetadata metadata = new CepperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_MATCHER);
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(3);
    metadata.setBucketSize(0);
    metadata.getParameters().put(MatcherWindow.PARAM_MATCHER_EXPRESSION, "evt[0]");
    Cepper cepper = new MoqlCepper("evt", metadata);
    cepper.addCepperListener(new CepperPrintListener());
    for(Object[] data : dataList) {
      cepper.operate(data);
    }
  }

  public void testTimeWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    CepperMetadata metadata = new CepperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_TIME);
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(3);
    metadata.setBucketDuration(2);
    Cepper cepper = new MoqlCepper("evt", metadata);
    cepper.addCepperListener(new CepperPrintListener());
    int i = 0;
    long curMills = System.currentTimeMillis();
    for(Object[] data : dataList) {
      cepper.operate(data);
      i++;
      if (i == 10) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        cepper.onTick();
        curMills = System.currentTimeMillis();
        i = 0;
      }
    }
  }
}
