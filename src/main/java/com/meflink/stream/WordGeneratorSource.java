package com.meflink.stream;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成单词，可当成一个实时打分程序.
 * (name, score, ts)
 */
public class WordGeneratorSource extends RichSourceFunction<Tuple3<String, Integer, Long>> {
    static final Logger LOG = LoggerFactory.getLogger(WordGeneratorSource.class);
    private volatile boolean running = true;

    @Override
    public void run(SourceContext ctx) throws Exception {
        while (running) {
            long ts = System.currentTimeMillis();
            int score = (int) (ts % 100);
            char name = (char) ('A' + score % 26);
            ctx.collect(new Tuple3<>(String.valueOf(name) + "_" + ts, score, System.currentTimeMillis()));
            try {
                Thread.sleep(30L);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public void cancel() {
        LOG.info("Stop....");
        running = false;
    }
}
