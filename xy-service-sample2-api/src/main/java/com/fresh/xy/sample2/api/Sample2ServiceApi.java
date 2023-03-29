package com.fresh.xy.sample2.api;

import com.fresh.common.result.JsonResult;
import com.fresh.common.result.PageJsonResultVo;
import com.fresh.xy.common.enums.ScanTypeEnum;
import com.fresh.xy.sample2.api.bo.Sample2ScanAddBo;
import com.fresh.xy.sample2.api.bo.Sample2ScanBo;
import com.fresh.xy.sample2.api.bo.Sample2ScanPageBo;
import com.fresh.xy.sample2.api.fallback.Sample2ServiceApiFallbackFac;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

//@FeignClient(name = "service-sample2", com.fresh.xy.sample2.api.fallback = Sample2ServiceApiFallback.class)
@FeignClient(name = "service-sample2", fallbackFactory = Sample2ServiceApiFallbackFac.class/*, configuration = {FlFeignClientConfiguration.class}*/)
public interface Sample2ServiceApi {

    @GetMapping("/sample2ScanApi/getById")
    JsonResult<Sample2ScanBo> getById(@RequestParam("id") Long id);

    @GetMapping("/sample2ScanApi/getByIds")
    JsonResult<List<Sample2ScanBo>> getByIds(@RequestParam(name = "ids", required = false) List<Long> ids);

    @GetMapping("/sample2ScanApi/listByPojo") //pojo参数在Get请求中的参数转化 @see ReflectiveFeign.create(Object[] argv), 支持度不高(declaredField, toString)
    JsonResult<PageJsonResultVo<Sample2ScanBo>> listByPojo(@SpringQueryMap Sample2ScanPageBo scanPageBo);

    @GetMapping("/sample2ScanApi/listByPojo2")//默认Feign not support Get RequestBody
    JsonResult<PageJsonResultVo<Sample2ScanBo>> listByPojo2(@RequestBody Sample2ScanPageBo scanPageBo);

    @GetMapping("/sample2ScanApi/listByParams") //@RequestParam标记的参数在Get请求中的参数转化 @see ReflectiveFeign.create(Object[] argv)
    JsonResult<PageJsonResultVo<Sample2ScanBo>> listByParams(@RequestParam("id") Long id, @RequestParam("scanType") ScanTypeEnum scanType, @RequestParam("scanTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime scanTime, @RequestParam("page") Long page, @RequestParam("pageSize") Long pageSize);

    @PostMapping("/sample2ScanApi/save")
    JsonResult save(@RequestBody Sample2ScanAddBo scanAddBo);

    //TODO hystrix配置特别是线程池配置使并发容量满足要求，feign配置，ribbon配置
}
