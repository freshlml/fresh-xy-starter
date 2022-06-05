package com.fresh.xy.redis.enums;


public enum PojoEnum {
    SYSTEM("SYSTEM", "系统");

    private String value;

    private String text;

    PojoEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


}
