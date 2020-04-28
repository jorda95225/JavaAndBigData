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
 * @description 固定时间间隔
 * @date 2020/4/28
 */
public class FixedDelayCheckpointTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //每隔10S做一次checkpoint
        env.enableCheckpointing(10000);
        //采用固定时间间隔重启策略，设置重启次数和重启时间间隔
        //发送前3次error，主程序自动重启不会挂掉，当重新发送第4次error的时候主程序达到重启上限，直接挂掉了
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3,// 尝试重启的次数
                Time.seconds(2)  //重启时间间隔
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