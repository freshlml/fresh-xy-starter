package com.fresh.xy.sample2.api.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fresh.xy.common.enums.ScanTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sample2ScanAddBo {
    @NotBlank(message = "name不能为空")
    private String name;
    @NotNull(message = "scanType不能为空")
    private ScanTypeEnum scanType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "scanTime不能为空")
    private LocalDateTime scanTime;
}
