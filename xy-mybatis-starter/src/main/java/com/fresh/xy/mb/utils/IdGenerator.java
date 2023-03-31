package com.fresh.xy.mb.utils;


import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class IdGenerator {

    public static long timestamp() {
        return Clock.systemDefaultZone().instant().toEpochMilli();
    }

    public static void main(String argv[]) {
        System.out.println(System.currentTimeMillis());
        System.out.println(Clock.systemDefaultZone().instant().toEpochMilli());
        System.out.println(LocalDateTime.now(ZoneOffset.of("+08:00:00")).toInstant(ZoneOffset.of("+08:00:00")).toEpochMilli());
    }

}
