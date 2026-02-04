package com.fresh.xy.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fresh.core.exception.BizException;
import com.fresh.xy.common.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@Slf4j
public class FlCustomSerializer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final String EMPTY_STRING = "";

    public FlCustomSerializer() {//note: 使用和RedisTemplate相同的ObjectMapper
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(JacksonUtils.defaultJavaTimeModule());
    }
    public FlCustomSerializer(boolean withoutType) {
        if(!withoutType) {
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        }
        objectMapper.registerModule(JacksonUtils.defaultJavaTimeModule());
    }

    public Object deserialize(String value) {
        if(value == null || value.length() == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(value, Object.class);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(String value, Class<T> clz) {
        if(value == null || value.length() == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(value, clz);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(String value, TypeReference<T> type) {
        try {
            return this.objectMapper.readValue(value, type);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(String value, JavaType type) {
        try {
            return this.objectMapper.readValue(value, type);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public Object deserialize(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, Object.class);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> clz) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, clz);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(byte[] bytes, TypeReference<T> type) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, type);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public <T> T deserialize(byte[] bytes, JavaType type) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, type);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not read JSON: " + ex.getMessage());
        }
    }

    public String serialize(Object value) {
        if(value == null) {
            return EMPTY_STRING;
        }
        try {
            return this.objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not write JSON: " + ex.getMessage());
        }
    }

    public byte[] serializeAsBytes(Object value) {
        if(value == null) {
            return EMPTY_ARRAY;
        }
        try {
            return this.objectMapper.writeValueAsBytes(value);
        } catch (Exception ex) {
            throw new BizException(() -> "Could not write JSON: " + ex.getMessage());
        }
    }


}
