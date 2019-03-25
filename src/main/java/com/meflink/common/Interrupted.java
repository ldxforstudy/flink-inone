package com.meflink.common;

public class Interrupted {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new IRun());
        t1.start();

        // 主线程过5秒后中断
        // Thread.sleep(5 * 1000);
        t1.interrupt();
        System.out.println("主线程退出");
    }

    static class IRun implements Runnable {
        int count;

        @Override
        public void run() {
            while (true) {
                System.out.println("--------------- " + count + ": " + System.currentTimeMillis());
                try {
                    // 休眠60秒
                    Thread.sleep(60 * 1000);
                    System.out.println("休眠后: " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    System.out.println("被中断: " + System.currentTimeMillis());
                    // Thread.currentThread().interrupt();
                    // break;
                }
                count++;
            }
        }
    }
}
