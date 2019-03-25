package com.meflink.common;

import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class JedisDo {
    public static final String HOST = "10.96.113.251";
    public static final int PORT = 6379;

    public static void main(String[] args) throws UnsupportedEncodingException {
        Jedis jedis = new Jedis(HOST, PORT);

//        jedis.setex("t1", 10, "v1");

        String s1 = "123";
        System.out.println(Charset.defaultCharset().name());
        byte[] b1 = s1.getBytes("UTF-8");
        jedis.setex("t2".getBytes(), 100, b1);

        System.out.println(jedis.get("t2"));
    }
}
