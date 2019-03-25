package com.meflink.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DataTimeUtil {

    public static void main(String[] args) {
        long ms = 1540451376L;
        ZonedDateTime dt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(ms), ZoneId.systemDefault());
        System.out.println(dt);

        LocalDateTime dt1 = LocalDateTime.ofInstant(Instant.ofEpochSecond(ms), ZoneId.systemDefault());
        System.out.println(dt1);

        ZoneId _8 = ZoneId.of("UTC+8");
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochSecond(ms), _8));
    }
}
