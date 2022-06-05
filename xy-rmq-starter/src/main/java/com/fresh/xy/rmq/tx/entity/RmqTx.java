package com.fresh.xy.rmq.tx.entity;

import com.fresh.xy.common.entity.BaseEntity;
import com.fresh.xy.common.enums.BizTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 事务回查的默认实现方式: 数据库中使用rmq_tx表
 * 1、如果没有事务回查，应该排除不必要的bean
 * @ComponentScan(basePackages = {"...", "..."}, excludeFilters = {@ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, classes = {RmqTxServiceImpl.class})})
 *
 * 2、如果需要使用默认事务回查
 *  1)、数据库中创建表rmq_tx
 *  2)、MybatisPlusConfig中配置Mapper扫描
 *  3)、配置文件中配置mapper映射文件扫描
 *
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RmqTx extends BaseEntity<Long> {
    private String txId;
    private BizTypeEnum bizType;
    private Long bizId;

    //本地事务执行成功后，status=0，而且事务消息可能已经成功commit了
    //本地事务执行成功后，status=0，假设事务消息还未commit，将执行事务回查，事务回查执行成功,status=1，执行失败,status=2
    private Integer status;//0-本地事务执行成功(事务回查未执行),1-事务回查执行成功,2-事务回查执行失败

}
