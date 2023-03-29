package com.fresh.xy.sample.api.fallback;

import com.fresh.common.result.JsonResult;
import com.fresh.xy.sample.api.SampleServiceApi;
import com.fresh.xy.sample.api.bo.SampleScanBo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class SampleServiceApiFallback implements FallbackFactory<SampleServiceApi> {

    @Override
    public SampleServiceApi create(Throwable cause) {
        return new SampleServiceApi() {
            @Override
            public JsonResult<SampleScanBo> getById(Long id) {
                cause.printStackTrace();
                return JsonResult.buildFailedResult("getById调用失败,SampleService");
            }
        };
    }
}
