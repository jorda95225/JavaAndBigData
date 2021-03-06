package com.jordan.split;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * @autheor masheng
 * @description SideOutput支持二级分流
 * @date 2020/4/28
 */
public class SideOutputTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> sourceData = env.readTextFile("D:\\project\\JavaAndBigData\\Flink\\src\\main\\resources\\sideOutputTest.txt");

        DataStream<PersonInfo> personStream = sourceData.map(new MapFunction<String, PersonInfo>() {
            @Override
            public PersonInfo map(String s) throws Exception {
                String[] lines = s.split(",");
                PersonInfo personInfo = new PersonInfo();
                personInfo.setName(lines[0]);
                personInfo.setProvince(lines[1]);
                personInfo.setCity(lines[2]);
                personInfo.setAge(Integer.valueOf(lines[3]));
                personInfo.setIdCard(lines[4]);
                return personInfo;
            }
        });

        //定义流分类标识  进行一级分流
        //这里是个类this needs to be an anonymous inner class, so that we can analyze the type
        OutputTag<PersonInfo> shandongTag = new OutputTag<PersonInfo>("shandong") {};
        OutputTag<PersonInfo> jiangsuTag = new OutputTag<PersonInfo>("jiangsu") {};

        SingleOutputStreamOperator<PersonInfo> splitProvinceStream = personStream.process(new ProcessFunction<PersonInfo, PersonInfo>() {

            @Override
            public void processElement(PersonInfo person, Context context, Collector<PersonInfo> collector)
                    throws Exception {
                if ("shandong".equals(person.getProvince())) {
                    context.output(shandongTag, person);
                } else if ("jiangsu".equals(person.getProvince())) {
                    context.output(jiangsuTag, person);
                }
            }
        });
        DataStream<PersonInfo> shandongStream = splitProvinceStream.getSideOutput(shandongTag);
        DataStream<PersonInfo> jiangsuStream = splitProvinceStream.getSideOutput(jiangsuTag);

        //对数据进行二级分流，这里只对山东的数据流进行二级分流，江苏流程也一样
        OutputTag<PersonInfo> jinanTag = new OutputTag<PersonInfo>("jinan") {};
        OutputTag<PersonInfo> qingdaoTag = new OutputTag<PersonInfo>("qingdao") {};

        SingleOutputStreamOperator<PersonInfo> cityStream = shandongStream.process(new ProcessFunction<PersonInfo, PersonInfo>() {
            @Override
            public void processElement(PersonInfo person, Context context, Collector<PersonInfo> collector)
                    throws Exception {
                if ("jinan".equals(person.getCity())) {
                    context.output(jinanTag, person);
                } else if ("qingdao".equals(person.getCity())) {
                    context.output(qingdaoTag, person);
                }
            }
        });
        DataStream<PersonInfo> jinan = cityStream.getSideOutput(jinanTag);
        DataStream<PersonInfo> qingdao = cityStream.getSideOutput(qingdaoTag);

        jinan.map(new MapFunction<PersonInfo, String>() {
            @Override
            public String map(PersonInfo personInfo) throws Exception {
                return personInfo.toString();
            }
        }).print("山东-济南二级分流结果:");
        qingdao.map(new MapFunction<PersonInfo, String>() {
            @Override
            public String map(PersonInfo personInfo) throws Exception {
                return personInfo.toString();
            }
        }).print("山东-青岛二级分流结果:");
        env.execute();
    }
}