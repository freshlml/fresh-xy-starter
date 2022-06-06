package com.fresh.xy.redis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fresh.xy.redis.enums.ForRedisTestPojoDtoEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ForRedisTestPojoDto {
    private Long id;
    private String name;
    @NotNull(message = "pojoType不能为空")
    private ForRedisTestPojoDtoEnum pojoType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pojoTime;

}
