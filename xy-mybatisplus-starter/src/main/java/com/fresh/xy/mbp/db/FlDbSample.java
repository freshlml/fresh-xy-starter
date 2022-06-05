package com.fresh.xy.mbp.db;

public class FlDbSample {
    /**
     * 接入mybatis-plus
     * mybatis-plus-boot-starter,jdbc,druid-spring-boot-starter
     * auto config: datasource,sqlSessionFactory,...
     *
     * 1、配置文件: public-database-config.yaml
     * spring:
     *   datasource:
     *     type: com.alibaba.druid.pool.DruidDataSource
     *     driver-class-name: com.mysql.cj.jdbc.Driver
     *     username: root
     *     password: 123456
     *     druid:
     *       driver-class-name: com.mysql.cj.jdbc.Driver
     *       initial-size: 10
     *       max-active: 50
     *       min-idle: 5
     *       max-wait: 20000
     *       validation-query: select 'x'
     *       validation-query-timeout: 600000
     *       test-on-borrow: false
     *       test-on-return: false
     *       test-while-idle: true
     *       time-between-eviction-runs-millis: 60000
     *       min-evictable-idle-time-millis: 300000
     * 2、bootstrap.yml配置
     *   spring:
     *     datasource:
     *     url: jdbc:mysql://127.0.0.1:3306/sample?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
     *
     * 3、mybatis-plus配置
     *  ##mybatis-plus start
     *  mybatis-plus:
     *    ##mapper映射文件扫描
     *    mapper-locations:
     *    - classpath*:...
     *    -classpath*:...
     *    ##mybatis plus通用枚举扫描
     *    typeEnumsPackage: com.**.enums
     *  ##mybatis-plus end
     * 4、MybatisPlusConfig配置类
     *
     *
     * auto-configuration applied after user-defined beans have been registered
     * self defined auto-configuration override auto-configuration
     *
     */

}
