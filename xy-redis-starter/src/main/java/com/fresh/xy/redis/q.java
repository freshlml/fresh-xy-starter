package com.fresh.xy.redis;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class q {

    private static Logger logger = LoggerFactory.getLogger(q.class);

    public static void main(String argv[]) throws InterruptedException {
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

        RLock lock = redissonClient.getLock("myLock");//非公平锁，可重入


        /*try {
            lock.lock();  //尝试获取锁，如果锁不可用，当前线程处于休眠状态,直到当前线程获取锁
            //获取锁之后，锁leaseTime取Config.lockWatchdogTimeout的值
            //Watch Dog会自动续期: 每隔30/3=10秒续到30秒
            logger.info("q run start");
            Thread.sleep(40000);
            logger.info("q run end");
        } finally {
            lock.unlock();//手动解锁
        }*/

        //lock(leaseTime)
        try {
            //尝试获取锁，如果锁不可用，当前线程处于休眠状态,直到当前线程获取锁
            lock.lock(30, TimeUnit.SECONDS);
            //获取锁之后，如果leaseTime超时，且当前线程还没有unlock，则将触发unLock
            //无Watch dog
            logger.info("q run start");
            Thread.sleep(40000);
            logger.info("q run end");
        } finally {
            lock.unlock();

            redissonClient.shutdown();
            redissonReactiveClient.shutdown();
        }


    }

}
