package com.fresh.xy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BizTypeEnum {
    SAMPLE_SCAN("sample_scan", "sample库sample_scan表");

    @EnumValue
    private String value;
    private String text;

    BizTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @JsonCreator
    public static BizTypeEnum getByValue(String value) {
        return Arrays.stream(BizTypeEnum.values()).filter(scan -> scan.getValue().equals(value)).findFirst().orElse(null);
    }

}
