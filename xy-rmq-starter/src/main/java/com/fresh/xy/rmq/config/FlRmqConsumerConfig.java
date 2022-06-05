package com.fresh.xy.rmq.config;

import com.fresh.common.exception.BizException;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(prefix = "cus-rocketmq", value = "name-server")
public class FlRmqConsumerConfig implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext applicationContext;
    private final static Logger log = LoggerFactory.getLogger(FlRmqTxProducerConfig.class);
    private StandardEnvironment environment;
    private FlRmqProperties properties;

    public FlRmqConsumerConfig(StandardEnvironment environment, FlRmqProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(FlRmqConsumer.class);
        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerAdapter);
        }
    }

    private void registerAdapter(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        FlRmqConsumer annotation = clazz.getAnnotation(FlRmqConsumer.class);

        if(annotation.consumeMode() == FlRmqConsumer.ConsumeMode.CONCURRENTLY && !MessageListenerConcurrently.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + MessageListenerConcurrently.class.getName());
        }

        if(annotation.consumeMode() == FlRmqConsumer.ConsumeMode.ORDERLY && !MessageListenerOrderly.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + MessageListenerOrderly.class.getName());
        }

        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(annotation.consumerName(), FlDefaultMQPushConsumerAdapter.class,
                () -> createDefaultMQPushConsumer(bean, annotation));
        //FlDefaultMQPushConsumerAdapter consumer = genericApplicationContext.getBean(annotation.consumerName(), FlDefaultMQPushConsumerAdapter.class);

        //try {
        //    consumer.start();
        //} catch (MQClientException e) {
        //   throw new BizException(e);
        //}

        log.info("Register the Push Consumer to ioc, beanName:{}, ConsumerBeanName:{}", beanName, annotation.consumerName());
    }

    private FlDefaultMQPushConsumerAdapter createDefaultMQPushConsumer(Object bean, FlRmqConsumer annotation) {
        //DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(annotation.group());
        FlDefaultMQPushConsumerAdapter adapter = new FlDefaultMQPushConsumerAdapter(annotation.group());

        adapter.setConsumerName(annotation.consumerName());
        adapter.setTopic(environment.resolvePlaceholders(annotation.topic()));
        adapter.setTagExp(environment.resolvePlaceholders(annotation.tagExp()));
        adapter.setConsumeMode(annotation.consumeMode());

        adapter.setNamesrvAddr(properties.getNameServer());
        adapter.setConsumeThreadMax(annotation.consumeThreadMax());
        if (annotation.consumeThreadMax() < adapter.getConsumeThreadMin()) {
            adapter.setConsumeThreadMin(annotation.consumeThreadMax());
        }

        switch (annotation.messageModel()) {
            case BROADCASTING:
                adapter.setMessageModel(MessageModel.BROADCASTING);
                break;
            case CLUSTERING:
                adapter.setMessageModel(MessageModel.CLUSTERING);
                break;
            default:
                throw new IllegalArgumentException("Property 'messageModel' was wrong.");
        }
        try {
            adapter.subscribe(environment.resolvePlaceholders(annotation.topic()), environment.resolvePlaceholders(annotation.tagExp()));
        } catch (MQClientException e) {
            throw new BizException(e);
        }

        switch (annotation.consumeMode()) {
            case ORDERLY:
                //adapter.setMessageListenerOrderly((MessageListenerOrderly) bean);
                adapter.setMessageListener((MessageListenerOrderly) bean);
                break;
            case CONCURRENTLY:
                //adapter.setMessageListenerConcurrently((MessageListenerConcurrently) bean);
                adapter.setMessageListener((MessageListenerConcurrently) bean);
                break;
            default:
                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
        }

        return adapter;
    }

}
