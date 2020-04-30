package com.jordan.source

import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

class MySensorSource extends SourceFunction[SensorReading]{
  var running = true
  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    val rand = new Random()
    var curTemp = 1.to(10).map(
      i => ("sensor_" + i, 65 + rand.nextGaussian() * 20)
    )
    while (running) {
      curTemp = curTemp.map(
        t => (t._1, t._2 + rand.nextGaussian())
      )
      val curTime = System.currentTimeMillis()
      curTemp.foreach(
        t => ctx.collect(SensorReading(t._1, curTime, t._2))
      )
      Thread.sleep(100)
    }
  }

  override def cancel(): Unit = {
    running = false
  }
}
