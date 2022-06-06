package com.fresh.xy.redis.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ForRedisTestSetDto {

    @NotBlank(message = "key不能为空")
    private String key;
    @NotNull(message = "value不能为空")
    private Object value;

}
