package com.fresh.xy.rmq.config;

import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(prefix = "cus-rocketmq", value = "name-server")
public class FlRmqTxProducerConfig implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private final Map<String, TransactionMQProducer> cache = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;
    private final static Logger log = LoggerFactory.getLogger(FlRmqTxProducerConfig.class);
    private StandardEnvironment environment;
    private FlRmqProperties properties;

    public FlRmqTxProducerConfig(StandardEnvironment environment, FlRmqProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(FlRmqTxProducer.class);
        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerAdapter);
        }
    }

    private void registerAdapter(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if(!TransactionListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + TransactionListener.class.getName());
        }
        FlRmqTxProducer annotation = clazz.getAnnotation(FlRmqTxProducer.class);

        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(annotation.producerName(), FlTransactionMQProducerAdapter.class,
                () -> createTransactionMQProducer(bean, annotation));
        //FlTransactionMQProducerAdapter producer = genericApplicationContext.getBean(annotation.producerName(), FlTransactionMQProducerAdapter.class);

        //or using cache
        //TransactionMQProducer producer = createTransactionMQProducer(bean, annotation);
        //cache.put(annotation.producerName(), producer);
        //try {
        //    producer.start();
        //} catch (MQClientException e) {
        //    throw new BizException(e);
        //}

        log.info("Register the Tx Producer to ioc, beanName:{}, ProducerBeanName:{}", beanName, annotation.producerName());
    }

    private FlTransactionMQProducerAdapter createTransactionMQProducer(Object bean, FlRmqTxProducer annotation) {
        //TransactionMQProducer txProducer = new TransactionMQProducer(annotation.group());
        FlTransactionMQProducerAdapter adapter = new FlTransactionMQProducerAdapter(annotation.group());

        adapter.setNamesrvAddr(properties.getNameServer());
        adapter.setProducerName(annotation.producerName());
        adapter.setTransactionListener((TransactionListener) bean);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(annotation.corePoolSize(), annotation.maximumPoolSize(),
                annotation.keepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(annotation.blockingQueueSize()));
        adapter.setExecutorService(executor);

        FlRmqProperties.FlProducer producerConfig = properties.getProducer();
        adapter.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        adapter.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        adapter.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        adapter.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        adapter.setMaxMessageSize(producerConfig.getMaxMessageSize());
        adapter.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

        return adapter;
    }

    @Override
    public void destroy() throws Exception {
        for (Map.Entry<String, TransactionMQProducer> kv : cache.entrySet()) {
            if (Objects.nonNull(kv.getValue())) {
                kv.getValue().shutdown();
            }
        }
        cache.clear();
    }
}
