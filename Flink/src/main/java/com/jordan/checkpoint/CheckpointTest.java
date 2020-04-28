package com.jordan.checkpoint;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @autheor masheng
 * @description 不设置checkpoint，默认是无重启策略，收到error信息直接挂掉
 * @date 2020/4/28
 */
public class CheckpointTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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