package com.jordan;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @autheor masheng
 * @description kafka生产者分析
 * @date 2020/5/19
 */
public class KafkaProducerTest {

    public static final String brokerList = "localhost:9092";
    public static final String topic = "test";

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "hello,Kafka!");
//        producer.send(record);
//        Future<RecordMetadata> future = producer.send(record);
//        RecordMetadata metadata = future.get();
//        System.out.println(metadata.topic() + "-" + metadata.partition() + "-" + metadata.offset());
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    System.out.println(metadata.topic() + "-" + metadata.partition() + "-" + metadata.offset());
                }
            }
        });
        producer.close();
    }
}