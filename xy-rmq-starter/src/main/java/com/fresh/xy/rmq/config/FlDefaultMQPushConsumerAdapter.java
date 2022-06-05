package com.fresh.xy.rmq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.util.List;

public class FlDefaultMQPushConsumerAdapter extends DefaultMQPushConsumer
        implements InitializingBean, DisposableBean, SmartLifecycle, ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(FlDefaultMQPushConsumerAdapter.class);

    private ApplicationContext applicationContext;
    private boolean running;
    private String consumerName;
    private String topic;
    private String tagExp;
    private FlRmqConsumer.ConsumeMode consumeMode;
    private MessageListenerConcurrently messageListenerConcurrently;
    private MessageListenerOrderly messageListenerOrderly;

    public FlDefaultMQPushConsumerAdapter(String group) {
        super(group);
    }

    public MessageListenerConcurrently getMessageListenerConcurrently() {
        return messageListenerConcurrently;
    }

    public void setMessageListenerConcurrently(MessageListenerConcurrently messageListenerConcurrently) {
        this.messageListenerConcurrently = messageListenerConcurrently;
    }

    public MessageListenerOrderly getMessageListenerOrderly() {
        return messageListenerOrderly;
    }

    public void setMessageListenerOrderly(MessageListenerOrderly messageListenerOrderly) {
        this.messageListenerOrderly = messageListenerOrderly;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTagExp() {
        return tagExp;
    }

    public void setTagExp(String tagExp) {
        this.tagExp = tagExp;
    }

    public FlRmqConsumer.ConsumeMode getConsumeMode() {
        return consumeMode;
    }

    public void setConsumeMode(FlRmqConsumer.ConsumeMode consumeMode) {
        this.consumeMode = consumeMode;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }


///    ApplicationContextAware
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
///    ApplicationContextAware

/// InitializingBean
    @Override
    public void afterPropertiesSet() throws Exception {
        /*switch (consumeMode) {
            case ORDERLY:
                this.setMessageListener(new FlDefaultMsgListenerOrderly());
                break;
            case CONCURRENTLY:
                this.setMessageListener(new FlDefaultMsgListenerConcurrently());
                break;
            default:
                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
        }*/
    }
/// InitializingBean

//// SmartLifecycle
    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {
        if (this.isRunning()) {
            log.warn("rmq Consumer adapter already running. " + this.toString());
            return ;
            //throw new IllegalStateException("rmq Consumer adapter already running. " + this.toString());
        }
        try {
            super.start();
        } catch (MQClientException e) {
            throw new IllegalStateException("Failed to start rmq Consumer adapter", e);
        }
        this.setRunning(true);

        log.info("running rmq Consumer adapter: {}", this.toString());
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        //this.shutdown(); //在destroy中执行
        setRunning(false);
        log.info("rmq Consumer adapter stopped: " + this.toString());
    }

    @Override
    public int getPhase() {
        // Returning Integer.MAX_VALUE only suggests that
        // we will be the first bean to shutdown and last bean to start
        return Integer.MAX_VALUE;
    }
///SmartLifecycle

    private void setRunning(boolean running) {
        this.running = running;
    }

//DisposableBean
    @Override
    public void destroy() {
        this.setRunning(false);
        super.shutdown();
        log.info("rmq Consumer adapter destroyed: " + this.toString());
    }
//DisposableBean

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{beanName=" + getConsumerName()
                + ", consumerGroup=" + super.getConsumerGroup()
                + ", namesrv=" + super.getNamesrvAddr()
                + ", topic=" + topic
                + ", tagExp=" + tagExp
                + ", messageModel=" + super.getMessageModel()
                + ", consumeMode=" + consumeMode;
    }


    public class FlDefaultMsgListenerConcurrently implements MessageListenerConcurrently {
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            return messageListenerConcurrently.consumeMessage(msgs, context);
        }
    }

    public class FlDefaultMsgListenerOrderly implements MessageListenerOrderly {

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            return messageListenerOrderly.consumeMessage(msgs, context);
        }
    }

}
