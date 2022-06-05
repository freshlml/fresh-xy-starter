package com.fresh.xy.rmq.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FlRmqTxProducer {

    String producerName();

    String group();

    int corePoolSize() default 1;

    int maximumPoolSize() default 2;

    long keepAliveTime() default 1000 * 60; //60ms

    int blockingQueueSize() default 2000;

}
