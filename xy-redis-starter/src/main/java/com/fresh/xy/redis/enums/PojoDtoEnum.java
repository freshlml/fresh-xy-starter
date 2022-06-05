package com.fresh.xy.redis.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PojoDtoEnum {
    SYSTEM("SYSTEM", "系统");

    private String value;
    private String text;

    PojoDtoEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @JsonCreator
    public static PojoDtoEnum getByValue(String value) {
        return Arrays.stream(PojoDtoEnum.values()).filter(scan -> scan.getValue().equals(value)).findFirst().orElse(null);
    }

}
