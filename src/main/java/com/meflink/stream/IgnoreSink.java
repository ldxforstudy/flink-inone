package com.meflink.stream;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class IgnoreSink extends RichSinkFunction<String> {

    @Override
    public void invoke(String value, Context context) {
        // 啥也不干
    }
}
