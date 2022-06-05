package com.fresh.xy.redis.config;


import com.fresh.common.exception.BizException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties({FlRedissonProperties.class})
public class RedissonConfig {


    @Bean(destroyMethod = "shutdown")
    /*@RefreshScope*/
    public RedissonClient redissonClient(FlRedissonProperties properties) {
        Config config = null;
        try {
            config = Config.fromYAML(properties.getConfig());
            //config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResourceAsStream("redisson-config.yml"));
        } catch (IOException e) {
            throw new BizException(() -> e.getMessage());
        }
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonReactiveClient redissonReactiveClient(FlRedissonProperties properties) {
        Config config = null;
        try {
            config = Config.fromYAML(properties.getConfig());
        } catch (IOException e) {
            throw new BizException(() -> e.getMessage());
        }
        return Redisson.createReactive(config);
    }

}
