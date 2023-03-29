package com.fresh.xy.sample.api.feign;

import feign.QueryMapEncoder;
import feign.codec.EncodeException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeContext;
import org.springframework.format.datetime.standard.DateTimeContextHolder;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

/**
 * QueryMapEncoder {@link FlFeignClientConfiguration}
 */
public class FlQueryMapEncoder implements QueryMapEncoder {
    private final Map<Class<?>, ObjectParamMetadata> classToMetadata = new HashMap<Class<?>, ObjectParamMetadata>();
    private final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Map<String, Object> encode(Object object) throws EncodeException {
        try {
            ObjectParamMetadata metadata = getMetadata(object.getClass());
            Map<String, Object> fieldNameToValue = new HashMap<>();
            for (Field field : metadata.objectFields) {
                Object value = field.get(object);
                if (value != null && value != object) {
                    if(value instanceof LocalDateTime) {
                        DateTimeFormat annotation = field.getDeclaredAnnotation(DateTimeFormat.class);
                        String pattern = null;
                        if(annotation != null) pattern = annotation.pattern();
                        DateTimeFormatter dateTimeFormatter = createDateTimeFormatter(pattern);
                        Locale locale = LocaleContextHolder.getLocale();
                        DateTimeFormatter formatterToUse = (locale != null ? dateTimeFormatter.withLocale(locale) : dateTimeFormatter);
                        DateTimeContext context = DateTimeContextHolder.getDateTimeContext();
                        DateTimeFormatter formatter = (context != null ? context.getFormatter(formatterToUse) : formatterToUse);
                        fieldNameToValue.put(field.getName(), formatter.format((LocalDateTime) value));
                    } else if(value instanceof Enum) {
                        fieldNameToValue.put(field.getName(), ((Enum<?>) value).name());
                    } else {
                        fieldNameToValue.put(field.getName(), value);
                    }
                }
            }
            return fieldNameToValue;
        } catch (IllegalAccessException e) {
            throw new EncodeException("Failure encoding object into query map", e);
        }
    }

    private DateTimeFormatter createDateTimeFormatter(String pattern) {
        if(pattern == null || pattern.isEmpty()) {
            pattern = DEFAULT_PATTERN;
        }
        String patternToUse = StringUtils.replace(pattern, "yy", "uu");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(patternToUse).withResolverStyle(ResolverStyle.STRICT);
        return dateTimeFormatter;
    }

    private ObjectParamMetadata getMetadata(Class<?> objectType) {
        ObjectParamMetadata metadata = classToMetadata.get(objectType);
        if (metadata == null) {
            metadata = ObjectParamMetadata.parseObjectType(objectType);
            classToMetadata.put(objectType, metadata);
        }
        return metadata;
    }

    private static class ObjectParamMetadata {

        private final List<Field> objectFields;

        private ObjectParamMetadata(List<Field> objectFields) {
            this.objectFields = Collections.unmodifiableList(objectFields);
        }

        private static ObjectParamMetadata parseObjectType(Class<?> type) {
            List<Field> fields = new ArrayList<Field>();
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fields.add(field);
            }
            return new ObjectParamMetadata(fields);
        }
    }

}
