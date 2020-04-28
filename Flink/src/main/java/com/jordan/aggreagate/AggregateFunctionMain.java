package com.jordan.aggreagate;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @autheor masheng
 * @description 定义聚合函数类和窗口类，进行商品访问量的统计，根据滑动时间窗口，按照访问量排序输出
 * @date 2020/4/28
 */
public class AggregateFunctionMain {
    //滑动窗口大小
    public static int windowSize = 6000;
    //滑动窗口间隔
    public static int windowSlider = 3000;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //从文件读取数据，也可以从socket读取数据
        DataStream<String> sourceData = env.readTextFile("D:\\project\\JavaAndBigData\\Flink\\src\\main\\resources\\ProductViewData2.txt");
        DataStream<ProductViewData> productViewData = sourceData.map(new MapFunction<String, ProductViewData>() {
            @Override
            public ProductViewData map(String value) throws Exception {
                String[] record = value.split(",");
                return new ProductViewData(record[0], record[1], Long.valueOf(record[2]), Long.valueOf(record[3]));
            }
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<ProductViewData>(){
            @Override
            public long extractAscendingTimestamp(ProductViewData element) {
                return element.timestamp;
            }
        });
        /*过滤操作类型为1  点击查看的操作*/
        DataStream<String> productViewCount = productViewData.filter(new FilterFunction<ProductViewData>() {
            @Override
            public boolean filter(ProductViewData value) throws Exception {
                if(value.operationType==1){
                    return true;
                }
                return false;
            }
        }).keyBy(new KeySelector<ProductViewData, String>() {
            @Override
            public String getKey(ProductViewData value) throws Exception {
                return value.productId;
            }
            //时间窗口 6秒  滑动间隔3秒
        }).timeWindow(Time.milliseconds(windowSize), Time.milliseconds(windowSlider))
                /*这里按照窗口进行聚合*/
                .aggregate(new MyCountAggregate(), new MyCountWindowFunction());
        //聚合结果输出
        productViewCount.print();

        env.execute("AggregateFunctionMain2");
    }

}