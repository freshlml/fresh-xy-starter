package com.fresh.xy.redis;


/**
 * redis sample
 * redis cluster; spring data redis; Lettuce; Redisson
 * Lettuce: 基于netty，nio/io多路复用; share connection and thread-safe; reactive
 * Redisson: 基于netty，nio/io多路复用; share connection and thread-safe; reactive; more advanced features
 */
public class FlRedisSample {

    /**
     * Redis的auto configuration {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
     *                           {@link org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration}
     * 第一: 使用Lettuce接入redis cluster
     *     1.redis cluster的配置加载生成 {@link org.springframework.data.redis.connection.RedisClusterConfiguration}
     *       spring:
     *         redis:
     *           cluster:
     *             nodes: 47.98.221.91:6379,47.98.221.91:6380,47.98.221.91:6381,47.98.221.91:6382,47.98.221.91:6383,47.98.221.91:6384
     *             max-redirects: 3
     *           password:
     *     2.其他redis配置信息加载生成 RedisProperties
     *       spring:
     *         redis:
     *           ##密码
     *           password:
     *           #commandTimeout（毫秒）
     *           timeout: 3000
     *           #redis cluster模式设置为0
     *           database: 0
     *           ##是否开启ssl/tls
     *           ssl: false
     *           ##connection url(当然cluster下已配置了nodes),overrides host,port and password, user is ignored
     *           url: redis://user:password@47.98.221.91:6379
     *           host: ...
     *           port: ...
     *           ##使用redis cluster的配置RedisProperties.Cluster
     *           cluster:
     *             nodes: 47.98.221.91:6379,47.98.221.91:6380,47.98.221.91:6381,47.98.221.91:6382,47.98.221.91:6383,47.98.221.91:6384
     *             max-redirects: 3
     *           ##使用Lettuce的配置RedisProperties.Lettuce
     *           lettuce:
     *             pool:
     *               #连接池最大连接数（使用负值表示没有限制）
     *               max-active: -1
     *               #连接池最大阻塞等待时间（使用负值表示没有限制）
     *               max-wait: -1
     *               #连接池中的最大空闲连接
     *               max-idle: 5
     *               #连接池中的最小空闲连接
     *               min-idle: 0
     *             shutdown-timeout:
     *
     *     3.构造LettuceConnectionFactory {@link org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory}
     *        在LettuceConnectionConfiguration(auto config类)中构造LettuceConnectionFactory
     *          @Bean
     * 	        @ConditionalOnMissingBean(RedisConnectionFactory.class)
     * 	        public LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources)
     * 			        throws UnknownHostException {
     * 			    //构造LettuceClientConfiguration
     * 		        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources,this.properties.getLettuce().getPool());
     * 		        //构造LettuceConnectionFactory
     * 		        return createLettuceConnectionFactory(clientConfig);
     * 	        }
     *       1).Lettuce的ClientResources，可以去定义ioThreadPoolSize,eventLoopGroupProvider,eventExecutorGroup等，{@link io.lettuce.core.resource.DefaultClientResources}
     *         适当时机应该考虑重定义ClientResources的bean
     *       2).ClientResources，RedisProperties的配置保存到LettuceClientConfiguration
     *       3).根据LettuceClientConfiguration,RedisClusterConfiguration 构造 LettuceConnectionFactory
     *     4.LettuceConnection,RedisConnection; LettuceClusterConnection,RedisClusterConnection
     *         1).RedisClusterConnection实现了redis cluster的跨集群命令，注意: redis cluster环境下使用的是 ClusterConnection
     *         2).RedisConnection:
     *              all LettuceConnection instances created by the LettuceConnectionFactory
     *              share the same thread-safe native connection for all non-blocking and non-transactional operations todo 哪些operations？？？
     *              如果每次使用新的连接，设置shareNativeConnection=false
     *     5.RedisTemplate
     *        相对于RedisConnection的参数和返回值byte,RedisTemplate做了high-level abstraction for Redis interactions: 序列化器,管理命令
     *        RedisTemplate中使用序列化器，json格式通常使用Jackson2JsonRedisSerializer: {@link RedisConfig}
     *        ClusterOperations,ValueOperations,HashOperations,ZSetOperations,ListOperations,SetOperations...
     *
     *        set操作没有返回值，判断是否写成功，即不断地读(如TCC中，try阶段set,confirm阶段get,cancel阶段del),get,del有返回值
     *        set/get/del命令超时通过如上commandTimeout配置
     *
     * TODO 配置从slave读 redis cluster模式下配置slave读??? LettuceClientConfiguration.readFrom(REPLICA_PREFERRED)
     *
     * TODO using unix domain socket; pub/sub; redis stream; redis transaction; redis pipeline; redis script; redis.support更多支持
     *
     * TODO reactive
     *
     */


    /**
     * 额外接入Redisson,using redisson-spring-boot-starter
     * RedissonAutoConfiguration: {@link org.redisson.spring.starter.RedissonAutoConfiguration}
     * 1.redisson的配置，配置加载生成 RedissonProperties
     *   spring:
     *     redis:
     *       redisson:
     *         ##指定配置文件
     *         file: classpath:redisson.yml
     *         config: |
     *           clusterServersConfig:  ##cluster配置
     *             idleConnectionTimeout: 10000
     *             connectTimeout: 10000
     *             timeout: 3000
     *             retryAttempts: 3
     *             retryInterval: 1500
     *             nodeAddresses:
     *             - "redis://47.98.221.91:6379"
     *             - "redis://47.98.221.91:6380"
     *             - "redis://47.98.221.91:6381"
     *             scanInterval: 1000
     *             pingConnectionInterval: 0
     *           threads: 16
     *           nettyThreads: 32
     *           transportMode: "NIO"
     * 2.构建 RedissonClient，RedissonConnectionFactory(是RedisConnectionFactory的实现)
     *   这将和LettuceConnectionFactory的自动配置混淆 link: 3.手动创建redisson客户端
     *   而实际上，只接入Redisson就可以覆盖之前的用法。。。
     * 3.手动创建redisson客户端
     *{@link RedissonConfig}
     *TODO 并且目前RedissonConfig中没有指定codec，即会采用默认的codec，如bucket中默认使用FstCodec，lock中会用到LongCodec
     *TODO 考虑1: 已在用的RedisTemplate和Redisson使用各自的序列化，不干扰
     *TODO 考虑2: RedisTemplate和Redisson都是用Jaskcon序列化并且使用相同配置的ObjectMapper
     *
     */

}
