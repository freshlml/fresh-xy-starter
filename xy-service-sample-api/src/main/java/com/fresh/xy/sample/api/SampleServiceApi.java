package com.fresh.xy.sample.api;

import com.fresh.common.result.JsonResult;
import com.fresh.xy.sample.api.bo.SampleScanBo;
import com.fresh.xy.sample.api.fallback.SampleServiceApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-sample", fallbackFactory = SampleServiceApiFallback.class)
public interface SampleServiceApi {

    @GetMapping("/sampleScanApi/getById")
    JsonResult<SampleScanBo> getById(@RequestParam("id") Long id);

}
