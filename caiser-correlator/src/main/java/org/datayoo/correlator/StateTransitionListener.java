package org.datayoo.correlator;

public interface StateTransitionListener {
  /**
   *
   * @param name 状态名
   * @param success 状态迁移是否成功，TURE表示成功
   * @param context 状态节点上下文，当状态为true，且状态逻辑非否定态时，context中的
   *                recordSet及originalData属性有非空值
   */
  void onTransition(String name, boolean success, StateContext context);

}
