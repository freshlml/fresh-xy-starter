package com.fresh.xy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

/**
 *Enum,使用@JsonFormat(shape = JsonFormat.Shape.OBJECT)标记整个Enum对象，即在序列化时将这个Enum序列化输出
 *          {value: "SYSTEM", text: "系统"} , Vo输出给前端时就是这种序列化输出
 *    使用@JsonCreator标记static方法通过字符串发现Enum实例, Dto中Enum接受前端输入的字符串然后发现对应的Enum实例
 *
 * 将Enum保存到redis中时，也要序列化，默认的序列化和web中json序列化行为一致(和Vo序列化输出一致)
 * 所以按照上述Enum的默认规则，则序列化存入redis的值是: {value: "SYSTEM", text: "系统"}
 *                        反序列化则报错，因为反序列化的配置是根据String查找Enum实例，此种情况，则:
 *   1).序列化时取Enum的value字段保存到Redis中，反序列化时按照String查找Enum实例，这样将保持原来的Enum默认规则不变，保存到Redis的bean做特殊处理
 *   2).去掉@JsonFormat，使用@JsonValue和@JsonCreator，这样序列化和反序列化都是走String
 *
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ScanTypeEnum {
    SYSTEM("SYSTEM", "系统");

    @EnumValue
    private String value;
    private String text;

    ScanTypeEnum(String value, String text) {
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
    public static ScanTypeEnum getByValue(String value) {
        return Arrays.stream(ScanTypeEnum.values()).filter(scan -> scan.getValue().equals(value)).findFirst().orElse(null);
    }

}
