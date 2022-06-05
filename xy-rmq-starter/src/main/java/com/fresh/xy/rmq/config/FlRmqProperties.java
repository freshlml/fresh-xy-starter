package com.fresh.xy.rmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "cus-rocketmq")
public class FlRmqProperties {

    private String nameServer;

    private FlProducer producer;

    private Map<String, FlTopic> topics;

    @Getter
    @Setter
    public static class FlTopic {
        private String name;
        private Map<String, FlTag> tags;
    }

    @Getter
    @Setter
    public static class FlTag {
        private String name;
        private String desc;
    }

    @Getter
    @Setter
    public static class FlProducer {
        private String group;
        private String txGroup;
        private int corePoolSize = 1;
        private int maximumPoolSize = 2;
        private long keepAliveTime = 60000;
        private int blockingQueueSize = 2000;
        private int sendMessageTimeout = 3000;
        private int compressMessageBodyThreshold = 1024 * 4;
        private int retryTimesWhenSendFailed = 2;
        private int retryTimesWhenSendAsyncFailed = 2;
        private boolean retryNextServer = false;
        private int maxMessageSize = 1024 * 1024 * 4;
    }


}
