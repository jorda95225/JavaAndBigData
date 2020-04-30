# 1.任务提交流程

![image-20200430142725568](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200430142725568.png)

# 2.转换算子

keyBy：基于key的hashCode重分区，同一个key只能在一个分区内处理，一个分区内可以有不同的key的数据

DataStream-->(keyBy)KeyedStream-->(滚动聚合操作，比如sum,min等)-->DataStream

min和minBy的区别：min只会对函数作用的字段取最小值，其他字段取第一个值，minBy会取函数字段最小的那一整条数据作为结果

# 3.富函数

富函数是DataStream API提供的一个函数类的接口，所有Flink函数类都有其Rich版本，它可以获取运行环境的上下文，并拥有一些生命周期方法，可以实现更复杂的功能。

- open（）方法是初始化方法，map等算子杯调用之前open（）会被调用
- close（）方法是生命周期中的最后一个调用方法，做一些清理工作
- getRuntimeContext（）方法提供了函数的RuntimeContext的一些信息，比如函数的并行度，任务名字，以及state状态











