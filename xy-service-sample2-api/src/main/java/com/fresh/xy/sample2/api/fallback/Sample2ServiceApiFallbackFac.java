package com.fresh.xy.sample2.api.fallback;

import com.fresh.common.result.JsonResult;
import com.fresh.common.result.PageJsonResultVo;
import com.fresh.xy.common.enums.ScanTypeEnum;
import com.fresh.xy.sample2.api.Sample2ServiceApi;
import com.fresh.xy.sample2.api.bo.Sample2ScanAddBo;
import com.fresh.xy.sample2.api.bo.Sample2ScanBo;
import com.fresh.xy.sample2.api.bo.Sample2ScanPageBo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class Sample2ServiceApiFallbackFac implements FallbackFactory<Sample2ServiceApi> {
    @Override
    public Sample2ServiceApi create(Throwable throwable) {
        return new Sample2ServiceApi() {
            @Override
            public JsonResult<Sample2ScanBo> getById(Long id) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("getById调用失败,Sample2Service");
            }

            @Override
            public JsonResult<List<Sample2ScanBo>> getByIds(List<Long> ids) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("getByIds调用失败,Sample2Service");
            }

            @Override
            public JsonResult<PageJsonResultVo<Sample2ScanBo>> listByPojo(Sample2ScanPageBo scanPageBo) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("lByPojo调用失败,Sample2Service");
            }

            @Override
            public JsonResult<PageJsonResultVo<Sample2ScanBo>> listByPojo2(Sample2ScanPageBo scanPageBo) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("listByPojo2调用失败,Sample2Service");
            }

            @Override
            public JsonResult<PageJsonResultVo<Sample2ScanBo>> listByParams(Long id, ScanTypeEnum scanType, LocalDateTime scanTime, Long page, Long pageSize) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("listByParams调用失败,Sample2Service");
            }

            @Override
            public JsonResult save(Sample2ScanAddBo scanAddBo) {
                throwable.printStackTrace();
                return JsonResult.buildFailedResult("save调用失败,Sample2Service");
            }
        };
    }
}
