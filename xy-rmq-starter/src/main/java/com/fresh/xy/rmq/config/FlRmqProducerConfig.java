package com.fresh.xy.rmq.config;

import com.fresh.common.utils.AssertUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConditionalOnProperty(prefix = "cus-rocketmq", value = "name-server")
public class FlRmqProducerConfig implements ApplicationContextAware, SmartInitializingSingleton,
        EnvironmentAware, DisposableBean {

    private final Map<String, DefaultMQProducer> cache = new ConcurrentHashMap<>();
    private Logger log = LoggerFactory.getLogger(FlRmqProducerConfig.class);
    private Environment environment;
    private ApplicationContext applicationContext;
    private FlRmqProperties properties;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public FlRmqProducerConfig(FlRmqProperties properties) {
        this.properties = properties;
    }

    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(FlRmqProducer.class);
        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerAdapter);
        }
    }

    private void registerAdapter(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        FlRmqProducer annotation = clazz.getAnnotation(FlRmqProducer.class);
        FlRmqProperties.FlProducer producerConfig = properties.getProducer();

        AssertUtils.ifTrue(annotation.clusterMode() == FlRmqProducer.ClusterMode.ROCKETMQ_CLUSTER
            && annotation.group().equals(producerConfig.getGroup()),() -> "clusterMode and group config error", null);

        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(annotation.producerName(), FlDefaultMQProducerAdapter.class,
                () -> createDefaultMQProducer(annotation));
        //FlDefaultMQProducerAdapter producer = genericApplicationContext.getBean(annotation.producerName(), FlDefaultMQProducerAdapter.class);

        //or using cache
        //DefaultMQProducer producer = createDefaultMQProducer(annotation);
        //cache.put(annotation.producerName(), producer);
        //try {
        //    producer.start();
        //} catch (MQClientException e) {
        //    throw new BizException(e);
        //}

        log.info("Register the Producer to ioc, beanName:{}, ProducerBeanName:{}", beanName, annotation.producerName());
    }

    private FlDefaultMQProducerAdapter createDefaultMQProducer(FlRmqProducer annotation) {
        //DefaultMQProducer producer = new DefaultMQProducer(annotation.group());
        FlDefaultMQProducerAdapter adapter = new FlDefaultMQProducerAdapter(annotation.group());

        adapter.setProducerName(annotation.producerName());
        FlRmqProperties.FlProducer producerConfig = properties.getProducer();
        adapter.setNamesrvAddr(properties.getNameServer());
        if(annotation.defaultConf()) {
            adapter.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
            adapter.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
            adapter.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
            adapter.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
            adapter.setMaxMessageSize(producerConfig.getMaxMessageSize());
            adapter.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        } else {
            adapter.setSendMsgTimeout(annotation.sendMessageTimeout());
            adapter.setCompressMsgBodyOverHowmuch(annotation.compressMessageBodyThreshold());
            adapter.setRetryTimesWhenSendFailed(annotation.retryTimesWhenSendFailed());
            adapter.setRetryTimesWhenSendAsyncFailed(annotation.retryTimesWhenSendAsyncFailed());
            adapter.setMaxMessageSize(annotation.maxMessageSize());
            adapter.setRetryAnotherBrokerWhenNotStoreOK(annotation.retryNextServer());
        }

        return adapter;
    }

    @Override
    public void destroy() throws Exception {
        for (Map.Entry<String, DefaultMQProducer> kv : cache.entrySet()) {
            if (Objects.nonNull(kv.getValue())) {
                kv.getValue().shutdown();
            }
        }
        cache.clear();
    }
}
