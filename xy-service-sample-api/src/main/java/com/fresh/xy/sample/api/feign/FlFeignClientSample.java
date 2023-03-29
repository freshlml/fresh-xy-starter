package com.fresh.xy.sample.api.feign;

import com.fresh.common.result.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * name属性指定FeignClient的名称，将依据该名称创建RibbonClient，该RibbonClient可以通过@RibbonClient配置?
 * 如果是Eureka应用，RibbonClient将根据该名称发现服务的url(spring.application.name=flFeignClientSample)
 * 或者在ribbon中禁用Eureka并且通过配置的形式指定ribbon的配置({@see service-sample/com.xy.sample.ribbon.FlRibbonClientSample})
 *  ribbon:
 *    eureka:
 *      enabled: false
 *  stores:z
 *    ribbon:
 *      listOfServers: https://www.springcloud.cc/,baidu.com
 *
 *
 * 1.configuration属性指定FeignClient的配置类
 * 默认配置类 {@link org.springframework.cloud.openfeign.FeignClientsConfiguration}, FeignClient由FeignClientsConfiguration中已有的配置以及自定义配置类的配置组成（其中后者将覆盖前者）
 * [[FlFeignClientConfiguration不需要用@Configuration进行注释
 *      但是，如果是的话，请注意将其从任何可能包含此配置的@ComponentScan中排除，
 *      因为它将成为feign.Decoder，feign.Encoder，feign.Contract等的默认来源
 *      可以通过将其与任何@ComponentScan或@SpringBootApplication放在单独的，不重叠的包中来避免这种情况，也可以在@ComponentScan中将其明确排除在外]]
 * 2.FeignClient的配置: application.yml
 * feign:
 *   client:
 *     config:
 *       feignName: ##如name = "flFeignClientSample"
 *         connectTimeout: 5000
 *         readTimeout: 5000
 *         loggerLevel: full
 *         errorDecoder: com.example.SimpleErrorDecoder
 *         retryer: com.example.SimpleRetryer
 *         requestInterceptors:
 *           - com.example.FooRequestInterceptor
 *           - com.example.BarRequestInterceptor
 *         decode404: false
 *         encoder: com.example.SimpleEncoder
 *         decoder: com.example.SimpleDecoder
 *         contract: com.example.SimpleContract
 *      etc..
 *  feign:
 *   client:
 *     config:
 *       default: ##使用default,对所有FeignClient有效,等同于@EnableFeignClients属性defaultConfiguration
 *         ...
 *  默认配置文件中配置覆盖配置类(feign.client.default-to-properties=false时反之)
 *
 *  note: 如果需要在RequestInterceptor中使用ThreadLocal绑定变量，则需要将Hystrix的线程隔离策略设置为SEMAPHORE，并且在Feign中禁用Hystrix
 *  feign:
 *   hystrix:
 *     enabled: false
 *  hystrix:
 *   command:
 *     default:
 *       execution:
 *         isolation:
 *           strategy: SEMAPHORE
 *
 *  com.fresh.xy.sample2.api.fallback,fallbackFactory属性 fallback逻辑
 *  feign:
 *    hystrix:
 *      enabled: true  ##开启feign的hystrix，并且类路径上有hystrix依赖
 *
 */
@FeignClient(name = "flFeignClientSample",
    configuration = {FlFeignClientConfiguration.class},
    fallback = FlFeignClientSampleFallback.class
)
public interface FlFeignClientSample {

    /**
     * Spring Cloud添加了对Spring MVC注解的支持，并支持使用Spring Web中默认使用的同一HttpMessageConverters
     * Feign还支持可插拔编码器和解码器
     * Spring Cloud集成了Ribbon和Eureka以在使用Feign时提供负载平衡的http客户端
     */

    @GetMapping(".../getById")
    JsonResult rpcGetById(@RequestParam("id") Long id); //get请求，请求参数为id=id
    @GetMapping("../getByIds")
    JsonResult rpcGetByIds(@RequestParam("ids") List<Long> ids); //get请求，请求参数为ids=id1,id2
    @GetMapping("../getByPojo")
    JsonResult rpcGetByPojo(PojoParam param); //get请求，pojo对象??? @SpringQueryMap?
    @PostMapping("../rpcInsert")
    JsonResult rpcInsert(@RequestBody PojoParam param); //post请求，pojo序列化成json字符串

}
