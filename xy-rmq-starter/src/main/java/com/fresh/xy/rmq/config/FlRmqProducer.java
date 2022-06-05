package com.fresh.xy.rmq.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FlRmqProducer {

    String producerName();

    ClusterMode clusterMode() default ClusterMode.ROCKETMQ_CLUSTER;

    String group() default "${cus-rocketmq.producer.group}";

    boolean defaultConf() default true;

    int sendMessageTimeout() default 3000;
    int compressMessageBodyThreshold() default 1024 * 4;
    int retryTimesWhenSendFailed() default 2;
    int retryTimesWhenSendAsyncFailed() default 2;
    boolean retryNextServer() default false;
    int maxMessageSize() default 1024 * 1024 * 4;

    enum ClusterMode {
        ROCKETMQ_CLUSTER, MULTI_CLUSTER
    }

}
