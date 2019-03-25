package com.meflink.common;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class JedisTests {
    @Test
    public void TestJedisHGet() {
        Jedis jedis = new Jedis("10.96.113.251");
        String v = jedis.hget("h_1", "abcd");
        System.out.println(v);
    }
}
