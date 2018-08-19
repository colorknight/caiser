package org.datayoo.cepper.core;

import org.datayoo.cepper.CepperListener;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;

import java.util.List;

/**
 * Created by tangtadin on 17/1/26.
 */
public class CepperPrintListener implements CepperListener {

  @Override public void onRecordSet(RecordSet recordSet, List originalData) {
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    StringBuffer sbuf = new StringBuffer();
    for (ColumnDefinition column : recordSetDefinition.getColumns()) {
      sbuf.append(column.getName());
      sbuf.append("    ");
    }
    System.out.println(sbuf.toString());
    for (Object[] record : recordSet.getRecords()) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < record.length; i++) {
        if (record[i] != null) {
          sb.append(record[i].toString());
        } else {
          sb.append("NULL");
        }
        sb.append(" ");
      }
      System.out.println(sb.toString());
    }
    System.out.println("------------------------------------------------");
  }
}
