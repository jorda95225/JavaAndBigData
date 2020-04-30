package com.jordan.transformation

import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object Test {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.fromCollection(List(
      (1,2,2),
      (1,1,0),
      (1,3,3),
      (1,1,2),
      (2,3,3),
      (2,1,4),
      (2,0,1),
      (2,3,5)
    ))
      .keyBy(0)
//      .minBy(2)
      .reduce((x,y) => (x._1,x._2.max(y._2),x._3.min(y._3)))//取第二个最大值，第三个最小值
      .filter(new FilterFunction[(Int, Int, Int)] {
        override def filter(value: (Int, Int, Int)): Boolean = {
          value._3 == 2
        }
      })
      .print()
    env.execute("transform")
  }
}
