package com.fresh.xy.rmq.config;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;


public class FlTransactionMQProducerAdapter extends TransactionMQProducer
        implements InitializingBean, DisposableBean, SmartLifecycle, ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(FlTransactionMQProducerAdapter.class);

    private ApplicationContext applicationContext;
    private boolean running;
    private String producerName;

    public FlTransactionMQProducerAdapter(String group) {
        super(group);
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
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
            log.warn("rmq tx Producer adapter already running. " + this.toString());
            return ;
            //throw new IllegalStateException("rmq tx Producer adapter already running. " + this.toString());
        }
        try {
            super.start();
        } catch (MQClientException e) {
            throw new IllegalStateException("Failed to start rmq tx Producer adapter", e);
        }
        this.setRunning(true);

        log.info("running rmq tx Producer adapter: {}", this.toString());
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
        log.info("rmq tx Producer adapter stopped: " + this.toString());
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
        log.info("rmq tx Producer adapter destroyed: " + this.toString());
    }
//DisposableBean

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{beanName=" + getProducerName()
                + ", producerGroup=" + super.getProducerGroup()
                + ", namesrv=" + super.getNamesrvAddr();
    }

}
