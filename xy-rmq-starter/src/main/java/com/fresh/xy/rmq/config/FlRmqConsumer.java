package com.fresh.xy.rmq.config;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FlRmqConsumer {

    String consumerName();

    String group();

    String topic();

    String tagExp();

    MessageModel messageModel() default MessageModel.CLUSTERING;

    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    enum ConsumeMode {
        CONCURRENTLY,ORDERLY
    }

    int consumeThreadMax() default 64;

}
