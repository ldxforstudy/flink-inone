package com.meflink.jobs;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.meflink.stream.WordGeneratorSource;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 每30秒输出过去1分钟的Top10.
 */
public class Example2MeJob {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(config);

        DataStream<Tuple3<String, Integer, Long>> rawStream = env.addSource(new WordGeneratorSource())
                .name("RealTimeScoreSource");

        rawStream.timeWindowAll(Time.seconds(60), Time.seconds(30))
                .process(new TopN(10))
                .name("TopN")
                .print();

        JobExecutionResult result = env.execute("Example2Me");

    }

    static final class MyMapper extends RichMapFunction<Tuple3<String, Integer, Long>, String> {

        @Override
        public String map(Tuple3<String, Integer, Long> value) throws Exception {
            return value.f0;
        }
    }

    static final class TopN extends ProcessAllWindowFunction<Tuple3<String, Integer, Long>, String, TimeWindow> {

        private int n;

        TopN(int n) {
            this.n = n;
        }


        @Override
        public void process(Context context, Iterable<Tuple3<String, Integer, Long>> elements, Collector<String> out) throws Exception {
            List<Tuple3<String, Integer, Long>> l = new LinkedList<>();
            for (Tuple3<String, Integer, Long> item : elements) {
                l.add(item);
            }

            Collections.sort(l, new Comparator<Tuple3<String, Integer, Long>>() {
                @Override
                public int compare(Tuple3<String, Integer, Long> o1, Tuple3<String, Integer, Long> o2) {
                    return o2.f1 - o1.f1;
                }
            });

            StringBuilder sb = new StringBuilder();
            sb.append("Window[")
                    .append(transfer2BeiJingTime(context.window().getStart()))
                    .append(",")
                    .append(transfer2BeiJingTime(context.window().getEnd()))
                    .append("]: ");
            int c = 0;
            for (Tuple3<String, Integer, Long> res : l) {
                sb.append("(")
                        .append(res.f0)
                        .append(",")
                        .append(res.f1)
                        .append(")")
                        .append(", ");
                c++;
                if (c == this.n) {
                    break;
                }
            }
            out.collect(sb.toString());
        }
    }

    static String transfer2BeiJingTime(long mills) {
        Instant instant = Instant.ofEpochMilli(mills);
        return instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
