package com.meflink.jobs;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Kafka数据流统计单词.
 */
public final class Kafka4WordCountJob {
    static final Logger LOG = LoggerFactory.getLogger(Kafka4WordCountJob.class);

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "10.179.116.104:9092,10.179.16.225:9092,10.179.91.108:9092");
        kafkaProps.put("group.id", "flink-kafka-01");
        kafkaProps.put("enable.auto.commit", "true");
        kafkaProps.put("auto.commit.interval.ms", "1000");
//        kafkaProps.put("auto.offset.reset", "earliest");
        kafkaProps.put("session.timeout.ms", "30000");
        kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer011<>("wordcount-01", new SimpleStringSchema(), kafkaProps));

        stream.print();
        env.execute("Doing WordCount from Kafka");
    }
}
