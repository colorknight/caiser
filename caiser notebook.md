# CAISER简介

​	caiser是一个基于moql技术，面向事件处理的开源工程。因此其所能处理的事件数据都必须是结构化描述的。caiser工程下目前有两个基于规则的事件处理模块，分别是：caiser-cepper以及caiser-correlator。

​	**caiser-cepper**是一个复杂事件处理开发引擎，其功能及理念类似与Esper，但待处理的数据是无模式的。所谓无模式即不用像Esper一样，事先指定待处理的数据的模式，对于事件类型比较多的应用场景更适用，开发也更简单。该开发引擎主要是一个解释执行引擎，其主类MoqlCepper中没有线程调度逻辑，可由其它应用程序对它进行封装调度。

​	**caiser-correlator**是一款在caiser-cepper基础上开发出来的基于状态机的事件处理引擎，它可用于描述更加复杂的事件分析场景。如：我们通常会认为如果有人在非工作时间登上某文件服务器，并拿走很多文件是一个值得关注的事情，这很可能意味着一次信息泄漏。那么在只有网络层数据分析手段时，我们会发现没有办法用caiser-cepper这样的事件处理引擎进行分析发现。因为网络层分析手段能发现用户的登录行为以及网络通讯行为，但这是两类数据的数据特征相差很大，很难用一个复杂事件处理规则进行描述。但用两个规则却很好描述，规则1发现有人非工作时间登录；规则2发现登录IP在随后的一段时间内产生了大量输出流量，输出流量因文件传输引起。以上两个规则并非独立存在，而是存在一定的逻辑先后关系，而这在caiser-cepper中是无法描述的。故此caiser-correlator应运而生。caiser-correlator采用状态机的理念，将每一个规则看作一个状态，状态间进行有向无环连接。当状态1满足条件后会跳转到状态2…直至整个分析场景满足条件归约或超时回滚。caiser-correlator采用多线程并行执行，可有效提升多场景实例的执行效率。

# 复杂事件处理(CEPPER)

​	caiser-cepper是caiser工程下的一个复杂事件处理模块。其功能及理念类似于Esper，但待处理的数据是无模式的。所谓无模式即不用像Esper一样，事先指定待处理的数据的模式，对于事件类型比较多的应用场景更适用，开发也更简单。

​	示例代码如下(注：该代码在caiser-cepper的测试类中可以找到)：

```
//构造测试数据，测试数据为对象数组列表。数据共5*20=100条
List<Object[]> dataList = DataSimulator.createDataList(5, 20);
//构造复杂事件处理规则
CepperMetadata metadata = new CepperMetadata();
//设置规则名
metadata.setName("cep1");
//设置规则的窗口类型，关于规则支持的窗口类型见后文
metadata.setWinType(SlideWindowEnum.SW_BATCH.name());
/*基于Moql语法设置匹配规则。该规则含义为以事件数组的0下标索引值做名字，对下标为2的值
*求和。当有任何一组值的和大于100时，则匹配规则，并输出name和sum两个字段。注：from
*关键字后跟两个evt，第一个表示事件流的名字，第二个表示别名。Moql语法要求必须有别名，
*参见Moql相关语法文档。
*/
metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
//设置窗口桶的个数 
metadata. setBucketCount(5);
/*设置桶大小，每个桶可以缓存的数据条目。每个窗口由bucketCount个桶组成，其容量为
* bucketCount*bucketSize。每当窗口滑动时，引发计算并按桶进行数据淘汰，即桶的大小约定了
*窗口的数据处理粒度。该示例表示窗口最多可以容纳50/10=5个桶，当第6桶数据来时，第1桶被
*淘汰，只保留最新的5桶数据
*/
metadata.setBucketSize(10);
/*创建复杂事件规则对象，其中参数evt表示传给cepper的事件流的名字。该名字是规则定义时
*moql语句中from后的表被命名为evt的原因。
*/
Cepper<Object[]> cepper = new MoqlCepper<Object[]>("evt", metadata);
//设置事件监听器，当有匹配条件的事件发生时通过监听器可以订阅到
cepper.addCepListener(new CepPrintListener());
//开始进行复杂事件处理
for(Object[] data : dataList) {
  cepper.operate(data);
}
```

​	caiser-cepper目前支持4种滑动窗口类型。每种窗口都是由**bucketCount**个桶(**bucket**)组成，其容量为**bucketCount*****bucketSize**。桶表示了窗口的最小处理粒度，每当有新的一桶数据产生时，就会引发一次计算(窗口已满的情况下)，同时会淘汰掉最老的那桶数据。

Ø  **BatchWindow**

​	批处理窗口，以事件的数目做为窗口滑动的触发条件。***bucketCount***表示窗口能容纳的桶的总量；***bucketSize***表示一桶能容纳的事件的总量。

Ø  **TimeWindow**

​	时间窗口，以时间做为窗口滑动的触发条件。***bucketCount***表示窗口能容纳的桶的总量；***bucketDuration***表示每桶能够容纳多长时间的事件，单位秒。

Ø  **BatchAndTimeWindow**

​	批处理与时间并行窗口，同时以事件数及时间做为窗口滑动的触发条件，即那个条件先满足，就用哪个条件触发窗口滑动及相关计算。***bucketCount***与***bucketSize***参数的相关含义见BatchWindow。***bucketDuration***属性约定了时间窗口的大小(见TimeWindow)。这意味着，当一个桶只要满足***bucketSize***属性或者***bucketDuration***属性设定的任一条件就会触发窗口滑动。

Ø  **MatcherWindow**

​	值匹配窗口，即当某个指定值发生改变时引发窗口滑动。***bucketCount***表示窗口能容纳的桶的总量；***bucketSize***在此时没有实际意义，设置为0。另外，在上例的代码中CeperMetadata还有一个Map类型的parameters属性没有介绍，各窗口可以通过该属性设置窗口运行时特有的参数。MatcherWindow有一个名为win.matcher.expression的参数，该参数是一个表达式，符合MOQL中Operand的相关定义。当用该表达式在事件流中计算出的值发生变化时，产生一个以新值为当前值的新桶，并触发窗口的滑动和相关计算。如：当对存储后的数据进行事件流回放时，由于事件已不能正常反映实时的流动情况，此时可以用该窗口对事件中的时间字段按秒进行匹配，可模拟出实时流动时的计算效果。