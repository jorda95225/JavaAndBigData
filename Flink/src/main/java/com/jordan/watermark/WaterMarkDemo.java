package com.jordan.watermark;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

import javax.annotation.Nullable;

/**
 * @autheor masheng
 * @description watermark
 * @date 2020/4/28
 */
public class WaterMarkDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置使用EventTime
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //设置并行度为1方便理解
        env.setParallelism(1);
        SingleOutputStreamOperator<String> sourceDS = env.socketTextStream("192.168.131.11", 9000)
                .filter(new FilterFunction<String>() {
                    @Override
                    public boolean filter(String s) throws Exception {
                        if (s == null || s.equals("")) {
                            return false;
                        }
                        String[] strings = s.split(",");
                        if (strings.length != 2) {
                            return false;
                        }
                        return true;
                    }
                });
        //map转换，第一个字段代表时间，第二个字段代表单词，第三个字段固定值出现了一次
        SingleOutputStreamOperator<Tuple3<Long, String, Integer>> wordDS = sourceDS.map(new MapFunction<String, Tuple3<Long, String, Integer>>() {
            @Override
            public Tuple3<Long, String, Integer> map(String value) throws Exception {
                String[] strings = value.split(",");
                return new Tuple3<Long, String, Integer>(Long.valueOf(strings[0]), strings[1], 1);
            }
        });
        //设置watermark
        SingleOutputStreamOperator<Tuple3<Long, String, Integer>> wordCount = wordDS.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<Tuple3<Long, String, Integer>>() {
            private Long currentMaxTimestamp = 0L;
            //最大允许的消息延迟是5秒
            private final Long maxOutOfOrderness = 5000L;

            @Nullable
            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
            }

            @Override
            public long extractTimestamp(Tuple3<Long, String, Integer> element, long previousElementTimestamp) {
                Long timestamp = element.f0;
                currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                return timestamp;
            }
            //这里根据第二个元素  单词进行统计 时间窗口是30秒  最大延时是5秒，统计每个窗口单词出现的次数
        }).keyBy(1)
                .timeWindow(Time.seconds(30))
                .sum(2);
        wordCount.print("\n单词统计：");
        env.execute("Window WordCount");
    }
}