package com.fresh.xy.rmq.tx;

public interface RmqTxListenerService<T extends RmqTxModel> {
    boolean executeLocalTx(T model);
    boolean checkLocalTx(String txId);
}
