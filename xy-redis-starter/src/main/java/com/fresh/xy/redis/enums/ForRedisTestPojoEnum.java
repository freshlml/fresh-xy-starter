package com.fresh.xy.redis.enums;


public enum ForRedisTestPojoEnum {
    SYSTEM("SYSTEM", "系统");

    private String value;
    private String text;

    ForRedisTestPojoEnum(String value, String text) {
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
