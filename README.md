# libra
Data Processing Engine

![image](https://github.com/tonatyw/image/blob/master/libra/engine.png)

如上图所示
整个数据处理引擎分为以下三个概念
+ task:任务，一个任务可以有多个处理器进行处理，处理器之间可以并行
+ processer：处理器，一个处理器可以有多个行为，只能串行执行
+ performance： 行为，每个行为都需要配置一个classpath，标识这个行为需要运行怎样的逻辑

所有的节点(performance支持自定义传值，可以在此配置需要的自定义参数) 如：  
```java
<performance name="lowerField" class="LowerField" fieldKey="table_name"></performance>
```  
其中`name`和`class`必填， 后面`fieldKey`为自定义参数，会在map中存入，使用时可在map中根据`table_name`获取

后续更新……
