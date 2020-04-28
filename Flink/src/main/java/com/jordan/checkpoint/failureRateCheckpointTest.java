package com.jordan.checkpoint;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @autheor masheng
 * @description 失败率重启
 * @date 2020/4/28
 */
public class failureRateCheckpointTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //每隔10S做一次checkpoint
        env.enableCheckpointing(10000);
        //设置失败率重启策略，下面设置2分钟内 最大失败次数3次 重试时间间隔10秒
        //2分钟内，收到第三次error时，主程序达到失败次数，直接挂掉。
        env.setRestartStrategy(RestartStrategies.failureRateRestart(
                3, //
                Time.minutes(2), //time interval for measuring failure rate
                Time.seconds(10) // delay
        ));
        //从socket接收数据
        DataStream<String> source = env.socketTextStream("hadoop11", 9000);
        DataStream<String> filterData=source.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                if(null==value||"".equals(value)){
                    return false;
                }
                return true;
            }
        });
        DataStream<Tuple2<String, Integer>> wordOne = filterData.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                //收到error开头的数据 手动抛出异常  进行测试
                if(value.startsWith("error")){
                    throw new Exception("收到error，报错！！！");
                }
                String[] lines = value.split(",");
                for(int i=0;i<lines.length;i++){
                    Tuple2<String,Integer> wordTmp=new Tuple2<>(lines[i],1);
                    out.collect(wordTmp);
                }
            }
        });
        DataStream<Tuple2<String, Integer>> wordCount = wordOne.keyBy(0).sum(1);
        wordCount.print();
        env.execute();
    }
}