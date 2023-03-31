package com.fresh.xy.mb.core;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class,
        method="prepare", args={Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取被代理对象(可能多层)
        StatementHandler statementHandler = realTarget(invocation.getTarget());

        //非SELECT语句和CallableStatementHandler 不分页
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if(mappedStatement.getSqlCommandType() != SqlCommandType.SELECT || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }

        //获取BoundSql
        //BoundSql boundSql = statementHandler.getBoundSql();
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        //获取入参，并解析入参
        Object parameterObject = boundSql.getParameterObject();
        if(parameterObject == null) {
            return invocation.proceed();
        }
        Page<?> page = null;
        String pageKey = null;
        if(parameterObject instanceof Page) {
            page = (Page<?>) parameterObject;
        } else if(parameterObject instanceof Map) {
            for(Map.Entry<?, ?> e : ((Map<?, ?>) parameterObject).entrySet()) {
                if(e.getValue() instanceof Page) {
                    try {
                        pageKey = (String) e.getKey();
                    } catch (ClassCastException ex) {
                        throw new PageArgumentException("Map中Page实例对象的key必须是String类型");
                    }
                    if(pageKey == null || pageKey.trim().equals("")) {
                        throw new PageArgumentException("Map中Page类实例对象的key不能为空");
                    }
                    if(pageKey.contains(".")) {
                        throw new PageArgumentException("Map中Page类实例对象的key不能包含'.'字符");
                    }
                    page = (Page<?>) e.getValue();
                    break;
                }
            }
        }
        if(page == null || page.getPageSize() < 0) {
            return invocation.proceed();
        }

        //todo: 执行count，写入page.total, page.pages

        //拼接limit ? ?, todo: dialect
        String sql = boundSql.getSql();
        sql += " limit ?, ?";
        metaObject.setValue("delegate.boundSql.sql", sql);

        Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
        List<ParameterMapping> parameterMappings = new ArrayList<>(boundSql.getParameterMappings());
        String pagePrefix = (pageKey == null) ? "" : pageKey + ".";
        parameterMappings.add(new ParameterMapping.Builder(configuration, pagePrefix + "offset", Long.class).build());
        parameterMappings.add(new ParameterMapping.Builder(configuration, pagePrefix + "pageSize", Long.class).build());
        metaObject.setValue("delegate.boundSql.parameterMappings", parameterMappings);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }

    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }

}
