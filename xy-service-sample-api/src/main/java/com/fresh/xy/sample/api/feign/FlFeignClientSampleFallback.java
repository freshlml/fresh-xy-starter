package com.fresh.xy.sample.api.feign;

import com.fresh.common.result.JsonResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlFeignClientSampleFallback implements FlFeignClientSample {

    @Override
    public JsonResult rpcGetById(Long id) {
        return null;
    }

    @Override
    public JsonResult rpcGetByIds(List<Long> ids) {
        return null;
    }

    @Override
    public JsonResult rpcGetByPojo(PojoParam param) {
        return null;
    }

    @Override
    public JsonResult rpcInsert(PojoParam param) {
        return null;
    }
}
