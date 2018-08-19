package org.datayoo.correlator;

public abstract class CorrelatorConstants {

  public static String DATA_STREAM_NAME = "datas";
  /*
   * 保留字。全局上下文，用于传递全局输入的属性。只读，作用域全局。
   */
  public static String RK_GCTX = "gctx";
  /*
   * 保留字。场景实例上下文，用于传递状态间复用属性。读写，作用域场景实例。
   */
  public static String RK_CTX = "ctx";
  /*
   * 保留字。状态结果集，用于传递状态的计算结果。读写，状态实例。
   */
  public static String RK_RECORDSET = "rs";
  /*
   * 保留字。产生状态结果集的原始数据，用于传递状态的计算结果。读写，状态实例。
   */
  public static String RK_ORIGINALDATA = "od";

}
