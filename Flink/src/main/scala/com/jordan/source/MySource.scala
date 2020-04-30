package com.jordan.source

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._


case class SensorReading(id: String, timestamp: Long, temperature: Double)

object MySource {
  def main(args: Array[String]): Unit = {
    //自定义source
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.addSource(new MySensorSource).print()
    env.execute()
  }
}
