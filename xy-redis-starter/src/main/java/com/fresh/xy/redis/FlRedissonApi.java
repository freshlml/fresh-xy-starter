package com.fresh.xy.redis;

import com.fresh.xy.redis.dto.RedissonPojoDto;
import com.fresh.xy.redis.enums.PojoDtoEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.codec.FstCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redisson core api
 * features:
 *  单机多线程并发工具 --> 协调分布式多机多线程并发工具, eg：ConcurrentMap:单机多线程Map,RMap: 多机多线程Map
 *  基于netty，nio/io多路复用
 *  响应式库: reactive,Rxjava
 *  提供对Redis各种组态形式的连接功能(单机,cluster,sentinel)
 *  对Redis命令能以同步发送、异步形式发送、异步流形式发送或管道形式发送的功能，LUA脚本执行处理，以及处理返回结果的功能
 *  将原生的Redis Hash，List，Set，String，Geo，HyperLogLog等数据结构封装为Java里大家最熟悉的映射（Map），列表（List），集（Set），通用对象桶（Object Bucket），地理空间对象桶（Geospatial Bucket），基数估计算法（HyperLogLog）;还提供了分布式的多值映射（Multimap），本地缓存映射（LocalCachedMap），有序集（SortedSet），计分排序集（ScoredSortedSet），字典排序集（LexSortedSet），列队（Queue），阻塞队列（Blocking Queue），有界阻塞列队（Bounded Blocking Queue），双端队列（Deque），阻塞双端列队（Blocking Deque），阻塞公平列队（Blocking Fair Queue），延迟列队（Delayed Queue），布隆过滤器（Bloom Filter），原子整长形（AtomicLong），原子双精度浮点数（AtomicDouble），BitSet等Redis原本没有的分布式数据结构
 *  Lock，联锁（MultiLock），读写锁（ReadWriteLock），公平锁（Fair Lock），红锁（RedLock），信号量（Semaphore），可过期性信号量（PermitExpirableSemaphore）和闭锁（CountDownLatch）
 *  分布式远程服务（Remote Service），分布式执行服务（Executor Service）和分布式调度任务服务（Scheduler Service）,MapReduce service
 *  接入Spring
 *  redisson node???
 *  ...
 *
 */
public class FlRedissonApi {


    public static void bytesStream() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://47.98.221.91:6379")
                .addNodeAddress("redis://47.98.221.91:6380")
                .addNodeAddress("redis://47.98.221.91:6381");

        //TODO Config配置

        //sync api(同步api)、async api(异步api)
        RedissonClient redissonClient = Redisson.create(config);
        //async stream api(异步流式api), eg:reactive api/Rx api
        RedissonReactiveClient redissonReactiveClient = Redisson.createReactive(config);

        try {

            //直接写bytes,使用redis的key-value存储，"将序列化和反序列化外放"
            //序列化1、使用FstCodec
            //序列化2、兼容性考虑，使用Jackson
            RBinaryStream bytesStream = redissonClient.getBinaryStream("bytesStream");

            BigDecimal bd = new BigDecimal("8.9999011231312312312323123123123123123123123123123123123123123123123123123234434541353453645364356421432423");
            RedissonPojoDto pojo = RedissonPojoDto.builder()
                   .id(1234534535354L)
                   .bl(false)
                   .s(null)
                   .name("just pojo哒哒哒")
                   .bd(bd)
                   .pojoType(PojoDtoEnum.SYSTEM.getValue())
                   .pojoTime(LocalDateTime.now())
                   .build();
            //FstCodec
            FstCodec fstCodec = new FstCodec();
            //1.序列化
            ByteBuf ens = fstCodec.getValueEncoder().encode(pojo);
            //2.写bytes
            byte[] bytes = new byte[ens.readableBytes()];
            ens.readBytes(bytes);
            bytesStream.set(bytes);
            //3.读取bytes
            byte[] bytesObj = bytesStream.get();
            //4.反序列化 TODO,State？
            ByteBuf buf = Unpooled.wrappedBuffer(bytesObj);
            Object des = fstCodec.getValueDecoder().decode(buf, null);
            RedissonPojoDto desObj = (RedissonPojoDto) des;
            //5.和RBucket<RedissonPojoDto>在Redis中保存的数据格式是一样的，因此，可以用bucket读binary保存的数据
            RBucket<RedissonPojoDto> pojoBucket = redissonClient.getBucket("bytesStream");
            RedissonPojoDto pojoObj = pojoBucket.get();

            //Jackson
            //TODO  JsonJacksonCodec中使用的ObjectMapper的默认配置和RedisTemplate中正在用的有些不同
            //TODO 1、将这两者统一起来?
            JsonJacksonCodec jacksonCodec = new JsonJacksonCodec();
            //1.序列化
            ByteBuf jens = jacksonCodec.getValueEncoder().encode(pojo);
            //2.写bytes
            byte[] jbytes = new byte[jens.readableBytes()];
            jens.readBytes(jbytes);
            bytesStream.set(jbytes);
            //3.读取bytes
            byte[] jbytesObj = bytesStream.get();
            //4.反序列化 TODO,State？
            ByteBuf jbuf = Unpooled.wrappedBuffer(jbytesObj);
            Object jdes = jacksonCodec.getValueDecoder().decode(jbuf, null);
            RedissonPojoDto jdesObj = (RedissonPojoDto) jdes;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            redissonClient.shutdown();
            redissonReactiveClient.shutdown();
        }

    }

    public static void bucket() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://47.98.221.91:6379")
                .addNodeAddress("redis://47.98.221.91:6380")
                .addNodeAddress("redis://47.98.221.91:6381");

        //TODO Config配置

        //sync api(同步api)、async api(异步api)
        RedissonClient redissonClient = Redisson.create(config);
        //async stream api(异步流式api), eg:reactive api/Rx api
        RedissonReactiveClient redissonReactiveClient = Redisson.createReactive(config);


        //Bucket,使用redis的key-value存储(类似于RedisTemplate的opsForValue)，value默认使用FstCodec序列化和反序列化
        //FstCodec: 10倍于JDK序列化性能而且与之100%兼容;完美的保存类型和还原类型;redis中保存的数据不直观
        //TODO 兼容性考虑，使用Jackson
        RBucket<Object> nullBucket = redissonClient.getBucket("NullBucket");
        nullBucket.set(null);
        Object nullObj = nullBucket.get();

        RBucket<Boolean> booleanBucket = redissonClient.getBucket("BooleanBucket");
        booleanBucket.set(true);
        Boolean booleanObj = booleanBucket.get();
        booleanBucket.set(false);
        booleanObj = booleanBucket.get();

        RBucket<Double> doubleBucket = redissonClient.getBucket("DoubleBucket");
        doubleBucket.set(2.332131231212445645674765756877867897853764645645645656546546456);
        Double doubleObj = doubleBucket.get();

        BigDecimal bd = new BigDecimal("8.9999011231312312312323123123123123123123123123123123123123123123123123123234434541353453645364356421432423");
        RBucket<BigDecimal> bigDecimalBucket = redissonClient.getBucket("BigDecimalBucket");
        bigDecimalBucket.set(bd);
        BigDecimal bdObj = bigDecimalBucket.get();

        RBucket<String> stringBucket = redissonClient.getBucket("StringBucket");
        stringBucket.set("pojo1");
        String stringObj = stringBucket.get();
        
        RBucket<LocalDateTime> localDateTimeBucket = redissonClient.getBucket("LocalDateTimeBucket");
        localDateTimeBucket.set(LocalDateTime.now());
        LocalDateTime localDateTimeObj = localDateTimeBucket.get();

        RedissonPojoDto pojo = RedissonPojoDto.builder()
                .id(1234534535354L)
                .bl(false)
                .s(null)
                .name("just pojo哒哒哒")
                .bd(bd)
                .pojoType(PojoDtoEnum.SYSTEM.getValue())
                .pojoTime(LocalDateTime.now())
                .build();
        RBucket<RedissonPojoDto> pojoBucket = redissonClient.getBucket("PojoBucket");
        pojoBucket.set(pojo);
        RedissonPojoDto pojoObj = pojoBucket.get();


        List<RedissonPojoDto> list = new ArrayList<>();
        list.add(pojo);
        RBucket<List<RedissonPojoDto>> pojoListBucket = redissonClient.getBucket("PojoListBucket");
        pojoListBucket.set(list);
        List<RedissonPojoDto> pojoListObj = pojoListBucket.get();


        Map<String, RedissonPojoDto> map = new HashMap<>();
        map.put("pojo1", pojo);
        RBucket<Map<String, RedissonPojoDto>> pojoMapBucket = redissonClient.getBucket("PojoMapBucket");
        pojoMapBucket.set(map);
        Map<String, RedissonPojoDto> pojoMapObj = pojoMapBucket.get();



        redissonClient.shutdown();
        redissonReactiveClient.shutdown();
    }

    public static void atomicLong() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://47.98.221.91:6379")
                .addNodeAddress("redis://47.98.221.91:6380")
                .addNodeAddress("redis://47.98.221.91:6381");

        //TODO Config配置

        //sync api(同步api)、async api(异步api)
        RedissonClient redissonClient = Redisson.create(config);
        //async stream api(异步流式api), eg:reactive api/Rx api
        RedissonReactiveClient redissonReactiveClient = Redisson.createReactive(config);


        //java.AtomicLong: 单机多线程原子操作；RAtomicLong: 多机多线程原子操作
        //在redis中使用key-value存储，value默认使用StringCodec序列化和反序列化
        RAtomicLong myLong = redissonClient.getAtomicLong("myLong");
        myLong.set(1L); //sync api: 等待写redis后返回
        RFuture<Long> two = myLong.addAndGetAsync(1L);//async api: 提交任务到线程池，返回Future，异步任务执行完成后，从Future获取执行结果
        two.whenComplete((v, e) -> {
            System.out.println(v);
        });


        RAtomicLongReactive myLong2 = redissonReactiveClient.getAtomicLong("myLong");
        Mono<Long> three = myLong2.get();
        System.out.println("current ThreadId: " + Thread.currentThread().getId());
        three.subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ", threadId: " + Thread.currentThread().getId());
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            System.out.println("订阅数据");
            subscription.request(Long.MAX_VALUE);
        });


        redissonClient.shutdown();
        redissonReactiveClient.shutdown();
    }

    public static void map() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://47.98.221.91:6379")
                .addNodeAddress("redis://47.98.221.91:6380")
                .addNodeAddress("redis://47.98.221.91:6381");

        //TODO Config配置

        //sync api(同步api)、async api(异步api)
        RedissonClient redissonClient = Redisson.create(config);
        //async stream api(异步流式api), eg:reactive api/Rx api
        RedissonReactiveClient redissonReactiveClient = Redisson.createReactive(config);


        //redis based impl of java.util.concurrent.ConcurrentHashMap
        //在redis中使用key-hash存储，hash-key,hash-value默认使用FstCodec序列化和反序列化
        RMap<String, RedissonPojoDto> map = redissonClient.getMap("myMap"); //key,一个key在redis cluster中打到一个槽节点，可以使用redisson的内部分片
        BigDecimal bd = new BigDecimal("8.9999011231312312312323123123123123123123123123123123123123123123123123123234434541353453645364356421432423");
        RedissonPojoDto pojo = RedissonPojoDto.builder()
                .id(1234534535354L)
                .bl(false)
                .s(null)
                .name("just pojo哒哒哒")
                .bd(bd)
                .pojoType(PojoDtoEnum.SYSTEM.getValue())
                .pojoTime(LocalDateTime.now())
                .build();
        map.put("pojo1", pojo);  //sync: hash-key,hash-value
        map.put("pojo2", pojo);  //sync: hash-key,hash-value
        RFuture<RedissonPojoDto> fut = map.putAsync("pojo3", pojo); //async: hash-key,hash-value
        fut.whenComplete( (c, e) -> {
            System.out.println("put suc: " + c);
        });
        Set<Map.Entry<String, RedissonPojoDto>> mapObjs = map.entrySet();


        redissonClient.shutdown();
        redissonReactiveClient.shutdown();
    }

    public static void locks() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://47.98.221.91:6379")
                .addNodeAddress("redis://47.98.221.91:6380")
                .addNodeAddress("redis://47.98.221.91:6381");

        //TODO Config配置

        //sync api(同步api)、async api(异步api)
        RedissonClient redissonClient = Redisson.create(config);
        //async stream api(异步流式api), eg:reactive api/Rx api
        RedissonReactiveClient redissonReactiveClient = Redisson.createReactive(config);



        //redis based impl of java.util.concurrent.Lock
        RLock lock = redissonClient.getLock("myLock");//非公平锁，可重入

        //watch dog: 默认Config.lockWatchdogTimeout=30s

        //lock()
        try {
            lock.lock();  //尝试获取锁，如果锁不可用，当前线程处于休眠状态,直到当前线程获取锁
            //获取锁之后，锁leaseTime取Config.lockWatchdogTimeout的值
            //Watch Dog会自动续期: 每隔30/3=10秒续到30秒
        } finally {
            lock.unlock();//手动解锁
        }

        //tryLock()
        boolean sucLock0 = false;
        try {
            do {
                sucLock0 = lock.tryLock(); //尝试获取锁，如果锁可用，返回true，如果锁不可用，返回false
            } while(!sucLock0);
            //获取锁之后，锁leaseTime取Config.lockWatchdogTimeout的值
            //Watch Dog会自动续期: 每隔30/3=10秒续到30秒
        } finally {
            lock.unlock();//手动解锁
        }


        //tryLock(waitTime)
        boolean sucLock = false;
        try {
            //尝试获取锁，如果锁可用，返回true，如果锁不可用，当前线程休眠
            //直到当前线程获取锁(返回true)或者当前线程被打断(InterruptedException)或者waitTime到了(返回false)
            if( sucLock = lock.tryLock(10, TimeUnit.SECONDS) ) {
                //获取锁之后，锁leaseTime取Config.lockWatchdogTimeout的值
                //Watch Dog会自动续期: 每隔30/3=10秒续到30秒
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
            //throw e;
        } finally {
            if(sucLock) {
                lock.unlock();
            }
        }


        //lock(leaseTime)
        try {
            //尝试获取锁，如果锁不可用，当前线程处于休眠状态,直到当前线程获取锁
            lock.lock(30, TimeUnit.SECONDS);
            //获取锁之后，如果leaseTime超时，且当前线程还没有unlock，则将触发unLock
            //无 Watch Dog
        } finally {
            lock.unlock();
        }


        //tryLock(waitTime,leaseTime)
        boolean sucLock2 = false;
        try {
            //尝试获取锁，如果锁可用，返回true，如果锁不可用，当前线程休眠
            //直到当前线程获取锁(返回true)或者当前线程被打断(InterruptedException)或者waitTime到了(返回false)
            if( sucLock2 = lock.tryLock(10, 30, TimeUnit.SECONDS) ) {
                //获取锁之后，如果leaseTime超时，且当前线程还没有unlock，则将触发unLock
                //无 Watch Dog
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //throw e;
        } finally {
            if(sucLock2) {
                lock.unlock();
            }
        }

        //Lock应用问题说明
        //1.每一个客户端加锁，lock的key都存在一个有效期，防止客户端节点挂掉之后，该锁得不到释放
        //2.客户端加锁可以启动watch dog机制不断续约，然后手动调用unlock
        //3.单机redis节点挂掉之后，锁将不可用
        //4.replica架构下，master到slave是异步复制，存在一个客户端在master加锁，还未同步到slave时master挂掉了
        //    slave被选举成为master后，其他客户端可以获取锁，则存在多个客户端端同时获得了锁的情况
        //5.多节点分布式锁实现方案: RedLock


        //分布式多节点时钟漂移


        //TODO RedLock方案


        //RLockReactive lock2 = redissonReactiveClient.getLock("lock2");




        redissonClient.shutdown();
        redissonReactiveClient.shutdown();
    }

    public static void main(String argv[]) throws Exception {

        //bytesStream();
        //bucket();
        atomicLong();
        //map();
        //locks();


    }



}
