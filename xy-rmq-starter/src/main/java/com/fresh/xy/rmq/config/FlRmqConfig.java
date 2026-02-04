package com.fresh.xy.rmq.config;

import com.fresh.core.exception.BizException;
import com.fresh.core.utils.AssertUtils;
import com.fresh.xy.common.constants.RmqConstants;
import com.fresh.xy.rmq.BeanUtils;
import com.fresh.xy.rmq.tx.RmqTxListenerService;
import com.fresh.xy.rmq.tx.RmqTxModel;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
@ConditionalOnProperty(prefix = "cus-rocketmq", value = "name-server")
@EnableConfigurationProperties({FlRmqProperties.class})
@Import({FlRmqTxProducerConfig.class, FlRmqConsumerConfig.class})
public class FlRmqConfig {

    @Bean(destroyMethod = "shutdown")
    public DefaultMQProducer defaultMQProducer(FlRmqProperties properties) {
        FlRmqProperties.FlProducer producerConfig = properties.getProducer();
        String nameServer = properties.getNameServer();
        String groupName = producerConfig.getGroup();
        AssertUtils.ifTrue(nameServer==null||nameServer.length()==0, () -> "[cus-rocketmq.name-server] must not be null", null);
        AssertUtils.ifTrue(groupName==null||groupName.length()==0, () -> "[cus-rocketmq.producer.group] must not be null", null);

        DefaultMQProducer producer = new DefaultMQProducer(groupName);

        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

        try {
            producer.start();
        } catch (MQClientException e) {
            throw new BizException(e);
        }
        return producer;
    }

    @Bean(destroyMethod = "shutdown")
    public TransactionMQProducer defaultTxMQProducer(FlRmqProperties properties) {
        FlRmqProperties.FlProducer producerConfig = properties.getProducer();
        String groupName = producerConfig.getTxGroup();
        AssertUtils.ifTrue(groupName==null||groupName.length()==0, () -> "[rocketmq.producer.tx-group] must not be null", null);

        TransactionMQProducer txProducer = new TransactionMQProducer(groupName);
        txProducer.setNamesrvAddr(properties.getNameServer());
        txProducer.setTransactionListener(new DefaultMQTxListener());

        ThreadPoolExecutor executor = new ThreadPoolExecutor(producerConfig.getCorePoolSize(), producerConfig.getMaximumPoolSize(),
                producerConfig.getKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(producerConfig.getBlockingQueueSize()));
        txProducer.setExecutorService(executor);

        txProducer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        txProducer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        txProducer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        txProducer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        txProducer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        txProducer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

        try {
            txProducer.start();
        } catch (MQClientException e) {
            throw new BizException(e);
        }
        return txProducer;
    }

    private static class DefaultMQTxListener implements TransactionListener {
        private Logger log = LoggerFactory.getLogger(DefaultMQTxListener.class);

        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            RmqTxModel rmqTx = null;
            try {
                rmqTx = (RmqTxModel) arg;
            } catch (Exception e) {
                log.error("本地事务执行失败，model对象类型转化失败");
                return LocalTransactionState.UNKNOW;
            }
            rmqTx.setTxId(msg.getTransactionId());
            String implName = msg.getProperty(RmqConstants.DEFAULT_RMQ_TX_SERVICE_IMPL);
            if(implName==null || implName.trim().length()==0) {
                log.error(RmqConstants.DEFAULT_RMQ_TX_SERVICE_IMPL + "属性不能为空值");
                return LocalTransactionState.UNKNOW;
            }
            RmqTxListenerService implBean = null;
            try {
                implBean = BeanUtils.getBean(implName, RmqTxListenerService.class);
            } catch(Exception e) {
                log.error("无法获取实例对象: type = {}, name= {}", RmqTxListenerService.class, implName);
                return LocalTransactionState.UNKNOW;
            }
            if(implBean == null) {
                log.error("实例对象为空, type = {}, name= {}", RmqTxListenerService.class, implName);
                return LocalTransactionState.UNKNOW;
            }
            try {
                boolean result = implBean.executeLocalTx(rmqTx);
                if(!result) return LocalTransactionState.UNKNOW;
            } catch (Exception e) {
                log.error("本地事务执行发生异常 {}", e.getMessage());
                return LocalTransactionState.UNKNOW;
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            String implName = msg.getProperty(RmqConstants.DEFAULT_RMQ_TX_SERVICE_IMPL);
            if(implName==null || implName.trim().length()==0) {
                log.error(RmqConstants.DEFAULT_RMQ_TX_SERVICE_IMPL + "属性不能为空值");
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            RmqTxListenerService implBean = null;
            try {
                implBean = BeanUtils.getBean(implName, RmqTxListenerService.class);
            } catch(Exception e) {
                log.error("无法获取实例对象: type = {}, name= {}", RmqTxListenerService.class, implName);
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            if(implBean == null) {
                log.error("实例对象为空, type = {}, name= {}", RmqTxListenerService.class, implName);
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            boolean result = implBean.checkLocalTx(msg.getTransactionId());
            if(result) return LocalTransactionState.COMMIT_MESSAGE;
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

}
