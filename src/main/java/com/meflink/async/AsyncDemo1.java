package com.meflink.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncDemo1 {

    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture
                .runAsync(() -> m1(), executorService)
                .thenAcceptAsync((ignore) -> m2(), executorService)
                .thenRun(() -> executorService.shutdown());
        System.out.println("main finished");
    }

    static void m1() {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
            System.out.println("m1");
        } catch (InterruptedException e) {
            // ignore
        }
    }

    static void m2() {
        try {
            TimeUnit.MILLISECONDS.sleep(4000L);
            System.out.println("m2");
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
