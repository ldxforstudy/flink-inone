package com.meflink.helper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class EventFlowHelper {
    public static void main(String[] args) throws Exception {

    }

    public static void export2file() throws IOException {
        Path p = Paths.get("/Users/didi/Documents/java_workspace/flink-inone/src/main/resources/testdata", "1.txt");
        final BufferedWriter bw = Files.newBufferedWriter(p, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        IntStream.range(1, 60).forEach(i -> {
            try {
                StringJoiner sj = new StringJoiner(",");
                sj.add(String.valueOf(i)).add(String.valueOf(System.currentTimeMillis()));
                bw.write(sj.toString());
                bw.newLine();
                TimeUnit.SECONDS.sleep(1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        bw.flush();
    }
}
