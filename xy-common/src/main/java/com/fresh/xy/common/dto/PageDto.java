package com.fresh.xy.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PageDto {
    private long page = 1;
    private long pageSize = 10;
}
