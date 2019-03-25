package com.meflink.jobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.meflink.stream.IgnoreSink;
import com.meflink.stream.WordGeneratorSource;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlerJob {
    static final Logger LOG = LoggerFactory.getLogger(ErrorHandlerJob.class);

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(config);

        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(2, 1000L));

        DataStream<Tuple3<String, Integer, Long>> rawStream = env.addSource(new WordGeneratorSource())
                .name("RealTimeScoreSource").uid("11");

        rawStream.filter(new MonitorFilter<>())
                .map(new RaiseExceptionAfterSomeCount())
                .addSink(new IgnoreSink());

        JobExecutionResult result = null;
        try {
            result = env.execute("ErrorHandler");
            LOG.info("result: {}", result);
        } catch (Exception e) {
            StringBuilder buf = new StringBuilder();
            // 当前时间
            LocalDateTime dateTime = LocalDateTime.now();
            buf.append(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            buf.append("\n");

            // 任务名称
            buf.append("[").append("Demo").append("]");
            buf.append("\n");

            // 异常描述
            Throwable throwable = e.getCause();
            buf.append("异常描述:").append(throwable.getMessage());
            buf.append("\n");

            // 异常原因，取最近的trace即可
            StackTraceElement latestTrace = throwable.getStackTrace()[0];
            buf.append("异常原因:").append("\n");
            buf.append("\t 文件名:").append(latestTrace.getFileName()).append("#").append(latestTrace.getLineNumber()).append("\n");
            buf.append("\t 类名:").append(latestTrace.getClassName()).append("\n");
            buf.append("\t 方法名:").append(latestTrace.getMethodName()).append("\n");

            LOG.info("{}", buf.toString());
            throw e;
        }

    }

    static final class RaiseExceptionAfterSomeCount extends RichMapFunction<Tuple3<String, Integer, Long>, String> {
        static final Logger LOG = LoggerFactory.getLogger(RaiseExceptionAfterSomeCount.class);

        private int count = 0;

        @Override
        public String map(Tuple3<String, Integer, Long> value) throws Exception {
            count++;
            if (count > 30) {
                throw new RuntimeException("I'am shutdown");
            }
            return value.f0;
        }

        @Override
        public void close() throws Exception {
            super.close();
        }
    }

    static final class MonitorFilter<T> extends RichFilterFunction<T> {
        static final Logger LOG = LoggerFactory.getLogger(MonitorFilter.class);

        @Override
        public boolean filter(T value) throws Exception {
            return true;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
        }

        @Override
        public void close() throws Exception {
            LOG.info("关闭Filter，调用路径:{}", Arrays.asList(Thread.currentThread().getStackTrace()));
        }
    }
}
