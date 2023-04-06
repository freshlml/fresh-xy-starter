package com.fresh.xy.mb.core;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> enumClass;
    private final Field valueField;

    public EnumTypeHandler(Class<E> enumClass) {
        this.enumClass = enumClass;
        Field[] fields = enumClass.getDeclaredFields();
        valueField = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(EnumValue.class)).findFirst().orElse(null);
        if(valueField == null) throw new EnumValueException("找不到@EnumValue注解标记的field");
    }

    private Object findEnumValue(Enum<?> parameter) {
        if(valueField != null) {
            try {
                valueField.setAccessible(true);
                return valueField.get(parameter);
            } catch (IllegalAccessException e) {
                throw new EnumValueException("无法获取@EnumValue注解标记的field的值");
            }
        } else {
            throw new EnumValueException("找不到@EnumValue注解标记的field");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object enumValue = findEnumValue(parameter); //notify: 如果parameter不为null, enumValue也不应该为null
        if(enumValue == null) {
            if(jdbcType == null) {
                jdbcType = JdbcType.OTHER;
            }
            try {
                ps.setNull(i, jdbcType.TYPE_CODE);
            } catch (SQLException e) {
                throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . " + "Cause: " + e, e);
            }
        } else {
            ps.setObject(i, enumValue);
        }
    }


    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }

        Object enumValue = rs.getObject(columnName);
        return getEnumValue(enumValue);
    }

    private E getEnumValue(Object enumValue) {
        E[] enumConstants = enumClass.getEnumConstants();
        for(E em : enumConstants) {
            Object enumValueCandidate = null;
            try {
                valueField.setAccessible(true);
                enumValueCandidate = valueField.get(em);
            } catch (IllegalAccessException e) {
                //continue;
            }
            if(enumValue.equals(enumValueCandidate)) {
                return em;
            }
        }

        return null;
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }

        Object enumValue = rs.getObject(columnIndex);
        return getEnumValue(enumValue);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }

        Object enumValue = cs.getObject(columnIndex);
        return getEnumValue(enumValue);
    }

}
