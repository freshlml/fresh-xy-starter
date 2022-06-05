package com.fresh.xy.rmq.tx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.xy.rmq.tx.entity.RmqTx;
import com.fresh.xy.rmq.tx.mapper.RmqTxMapper;
import com.fresh.xy.rmq.tx.service.RmqTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RmqTxServiceImpl extends ServiceImpl<RmqTxMapper, RmqTx> implements RmqTxService {

}
