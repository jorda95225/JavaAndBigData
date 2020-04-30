package com.jordan.wc

import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment}
import org.apache.flink.api.scala._

object WordCount {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
//    val inputDS: DataSet[String] = env.readTextFile(args(0))
    println(111)
//    val wordCount: AggregateDataSet[(String, Int)] = inputDS.flatMap(_.split(" ")).map((_, 1)).groupBy(0).sum(1)
//    wordCount.writeAsCsv(args(1))
  }

}
