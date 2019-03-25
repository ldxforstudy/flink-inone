package com.meflink.jobs;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.meflink.stream.IgnoreSink;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTimeJob {
    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(config);

        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        URL fileUrl = EventTimeJob.class.getClassLoader().getResource("testdata/1.txt");
        Path filePath = Path.fromLocalFile(new File(fileUrl.toURI()));

        PojoTypeInfo<EventFlowPoJo> typeInfo = (PojoTypeInfo<EventFlowPoJo>) TypeExtractor.createTypeInfo(EventFlowPoJo.class);
        String[] fieldOrder = new String[]{"name", "timestamp"};
        PojoCsvInputFormat<EventFlowPoJo> csvInputFormat = new PojoCsvInputFormat<>(filePath, typeInfo, fieldOrder);

        DataStream<EventFlowPoJo> stream = env.createInput(csvInputFormat, typeInfo);
        stream.assignTimestampsAndWatermarks(new MyWaterMark(4))
                .timeWindowAll(Time.seconds(3))
                .process(new MyWindowProcess())
                .map(new PrintMapper())
                .addSink(new IgnoreSink());


        env.execute("DDD");
    }

    static class MyWaterMark extends BoundedOutOfOrdernessTimestampExtractor<EventFlowPoJo> {

        static final Logger LOG = LoggerFactory.getLogger(MyWaterMark.class);

        public MyWaterMark(int seconds) {
            super(Time.seconds(seconds));
        }

        @Override
        public long extractTimestamp(EventFlowPoJo element) {
            LOG.error("水印: " + element.timestamp);
            return element.timestamp;
        }
    }

    static class MyWindowProcess extends ProcessAllWindowFunction<EventFlowPoJo, String, TimeWindow> {

        @Override
        public void process(Context context, Iterable<EventFlowPoJo> elements, Collector<String> out) throws Exception {
            TimeWindow w = context.window();
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(Instant.ofEpochMilli(w.getStart()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            sb.append(",");
            sb.append(Instant.ofEpochMilli(w.getEnd()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            sb.append("] ");
            for (EventFlowPoJo pojo : elements) {
                sb.append(pojo.name).append(",");
            }
            out.collect(sb.toString());
        }
    }

    static class PrintMapper extends RichMapFunction<String, String> {
        static final Logger LOG = LoggerFactory.getLogger(PrintMapper.class);
        @Override
        public String map(String value) throws Exception {
            LOG.error(value);
            return value;
        }
    }
}
