package com.fresh.xy.rmq;

public class FlRocketmqSample {

    /**
     *第一: 使用rocketmq-spring-boot-starter
     * https://github.com/apache/rocketmq-spring/wiki
     *
     * rocketmq配置类: {@link org.apache.rocketmq.spring.autoconfigure.RocketMQProperties}
     * rocketmq:
     *   name-server: 127.0.0.1:19876;127.0.0.1:29876
     *   producer:
     *     group: sample-producer-group
     *     send-message-timeout: 3000
     *     compress-message-body-threshold: 4096
     *     retry-times-when-send-failed: 2
     *     retry-times-when-send-async-failed: 0
     *     retry-next-server: false
     *     max-message-size: 4194304
     *     access-key:
     *     secret-key:
     *     enable-msg-trace: true
     *     customized-trace-topic:
     *
     * DefaultMQProducer,RocketMQTemplate,{@link org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration}
     * 使用RocketMQProperties配置项创建DefaultMQProducer类
     * 创建RocketMQTemplate{DefaultMQProducer,ObjectMapper,SelectMessageQueueByHash,Map<String, TransactionMQProducer>}
     * 使用RocketMQTemplate封装DefaultMQProducer，实现消息发送/接收，消息序列化/反序列化
     *
     * @RocketMQMessageListener
     * {@link org.apache.rocketmq.spring.autoconfigure.ListenerContainerConfiguration} 解析@RocketMQMessageListener注解的bean
     * 启动rocketmq的PushConsumer
     *
     *
     * @RocketMQTransactionListener
     * {@link org.apache.rocketmq.spring.config.RocketMQTransactionAnnotationProcessor} 解析@RocketMQTransactionListener注解的bean
     * 创建rocketmq的TransactionMQProducer，封装在RocketMQTemplate的Map<String, TransactionMQProducer>，其中key时producer group
     *
     *
     *第二: 手动实现
     *  0、配置
     *     cus-rocketmq:
     *       name-server: 127.0.0.1:19876;127.0.0.1:29876
     *       ...
     * 配置映射在 {@link FlRmqProperties}
     *
     * 1、topic
     * 一个应用可以定义若干topic(按照一定划分规则,但通常一个就够了)，一个topic定义若干tag
     * topic命名规范: 应用名[-前缀]-topic；eg: sample-topic, sample-other-topic
     * 2、tx topic
     * 一个应用可以定义若干tx topic(按照一定划分规则,但通常一个就够了)，一个tx topic定义若干tag，tx topic用于发送tx消息
     * topic命名规范: 应用名-tx[-前缀]-topic；eg: sample-tx-topic, sample-tx-other-topic
     * #####topic的定义通过配置实现
     *   cus-rocketmq:
     *     topics:
     *       sample-topic:
     *         name: sample-topic
     *         tags:
     *           scan-update:
     *             name: scan-update
     *             desc: scan-update desc
     *           scan-insert:
     *             name: scan-insert
     *             desc: scan-insert desc
     *       sample-tx-topic:
     *         name: sample-tx-topic
     *         tags:
     *           tx-scan-update:
     *             name: tx-scan-update
     *             desc: tx-scan-update desc
     * 代码中取topic: FlRmqProperties.getTopics().get("sample-topic").getName();
     * 代码中取tag:   FlRmqProperties.getTopics().get("sample-topic").getTags().get("scan-update").getName();
     * 注解中配置placeholder @FlRmqConsumer(...,tagExp="${cus-rocketmq.topics.sample-topic.name} || tag_a",...)
     *
     *
     * 3、Producer实例
     * 一个应用可以定义若干Producer实例(通常一个就够了),每一个Producer实例使用一个Producer group
     *  这些Producer实例用于发送消息，至于把消息发送到哪个topic，由发送时候的代码决定
     *  Producer实例属于一个Producer group,同一个jvm中(同一节点)一个Producer group只能对应一个Producer实例
     *  一个应用如果集群部署，多个jvm中(不同节点)的相同Producer实例属于同一个Producer group
     * Producer实例命名: defaultMQProducer{@link FlRmqConfig}, 其他: [前缀]MQProducer，eg: otherMQProducer
     * Producer group命名: 应用名[-前缀]-producer-group，eg: sample-producer-group, sample-other-producer-group
     * #####在{@link FlRmqConfig}中，使用如下配置定义defaultMQProducer
     *     cus-rocketmq:
     *       producer:
     *         group: sample-producer-group
     *         send-message-timeout: 3000
     *         compress-message-body-threshold: 4096
     *         retry-times-when-send-failed: 2
     *         retry-times-when-send-async-failed: 0
     *         retry-next-server: false
     *         max-message-size: 4194304
     * #####自定义Producer实例,使用注解{@link FlRmqProducer}定义,注解解析{@link FlRmqProducerConfig}注册Producer,引用自动创建的Producer实例时使用@Lazy
     *
     *
     * 4、tx Producer实例
     * 一个应用定义若干tx Producer，每一个tx Producer实例使用一个tx Producer group
     *  这些Producer实例用于发送消息，至于把消息发送到哪个topic，由发送时候的代码决定
     *  tx Producer实例属于一个Producer group,同一个jvm中(同一节点)一个tx Producer group只能对应一个tx Producer实例
     *  一个应用如果集群部署，多个jvm中(不同节点)的相同tx Producer实例属于同一个tx Producer group
     * tx Producer实例命名: defaultTxMQProducer{@link FlRmqConfig}, 其他: [前缀]TxMQProducer，eg: scanTxMQProducer
     * tx Producer group命名: 应用名-tx[-前缀]-producer-group, eg: sample-tx-producer-group, sample-tx-scan-producer-group
     * #####在{@link FlRmqConfig}中，使用如下配置定义defaultTxMQProducer
     *     cus-rocketmq:
     *       producer:
     *         tx-group: sample-tx-producer-group
     *         core-pool-size: 1
     *         maximum-pool-size: 2
     *         keep-alive-time: 60000
     *         blocking-queue-size: 2000
     *         send-message-timeout: 3000
     *         compress-message-body-threshold: 4096
     *         retry-times-when-send-failed: 2
     *         retry-times-when-send-async-failed: 0
     *         retry-next-server: false
     *         max-message-size: 4194304
     * #####自定义tx Producer实例，使用注解{@link FlRmqTxProducer}定义,解析注解{@link FlRmqTxProducerConfig}注册Tx Producer,引用自动创建的tx Producer实例时使用@Lazy
     *
     *
     * 5、消费者定义
     * 假设现在要消费 topic[tag-a,tag-b]; 如果tag-a,tag-b分开来消费
     *   1)、定义一个消费组，里面的消费者有些消费tag-a,有些消费tag-b，这是有问题的
     *   2)、定义两个消费组，consume-group-a,consume-group-b
     *         consume-group-a中的所有消费者订阅topic, tag-a
     *         consume-group-b中的所有消费者订阅topic, tag-b
     *   即，一个消费组内的消费组消费行为要一致
     *   Consumer实例属于一个Consumer group,同一个jvm中(同一节点)一个Consumer group只能对应一个Consumer实例
     *   一个应用如果集群部署，多个jvm中(不同节点)的相同Consumer实例属于同一个Consumer group
     *   Consumer实例命名: [前缀]MQConsumer，eg: sampleTopicScanUpdateMQConsumer
     *   Consumer group命名: 应用名[-前缀]-consumer-group，eg: sample-topic-scan-update-consumer-group
     * #####使用注解{@link FlRmqConsumer}定义，注解解析{@link FlRmqConsumerConfig}注册Push Consumer
     *
     *
     * Lifecycle, @see
     * ArchIsomer/FlLifecycleTest
     *
     *
     *
     */





}
