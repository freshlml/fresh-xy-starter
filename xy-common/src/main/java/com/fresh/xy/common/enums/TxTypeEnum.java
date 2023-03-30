package com.fresh.xy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TxTypeEnum {
    ONE_REQUIRED("ONE_REQUIRED", "required for one"),
    ONE_REQUIRED_NEW("ONE_REQUIRED_NEW", "required_new for one"),
    ONE_NESTED("ONE_NESTED", "nested for one"),
    TWO_REQUIRED("TWO_REQUIRED", "required for two"),
    TWO_REQUIRED_NEW("TWO_REQUIRED_NEW", "required_new for two"),
    TWO_NESTED("TWO_NESTED", "nested for two");

    @EnumValue
    private String value;
    private String text;

    @JsonValue
    public String getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    @JsonCreator
    public static TxTypeEnum getByValue(String v) {
        TxTypeEnum result = Arrays.stream(TxTypeEnum.values()).filter(txType -> txType.value.equals(v)).findFirst().orElse(null);
        return result;
    }

    TxTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
