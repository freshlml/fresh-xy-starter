package com.fresh.xy.mb.core;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

@Intercepts({@Signature(type = StatementHandler.class,
        method="prepare", args={Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取被代理对象(可能多层)
        StatementHandler statementHandler = (StatementHandler) realTarget(invocation.getTarget());

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
        Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
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

        //count
        Executor executor = (Executor) metaObject.getValue("delegate.executor"); //?

        String countSql = "select count(*) from ( " + boundSql.getSql() + " ) alias"; //todo, sql分析，创建count sql
        BoundSql countBoundSql = new BoundSql(configuration, countSql, boundSql.getParameterMappings(), parameterObject); //additionalParameters?

        List<ResultMap> count_resultMaps = new ArrayList<ResultMap>();
        ResultMap count_resultMap = new ResultMap.Builder(configuration, mappedStatement.getId() + "_count" + "-inline", Long.class, new ArrayList<>()).build();
        count_resultMaps.add(count_resultMap);
        SqlSource countSqlSource = new StaticSqlSource(configuration, countSql, boundSql.getParameterMappings());
        MappedStatement count_ms = new MappedStatement.Builder(configuration, mappedStatement.getId() + "_count", countSqlSource, SqlCommandType.SELECT)
                .resultMaps(count_resultMaps)
                .fetchSize(mappedStatement.getFetchSize()) //?
                .timeout(mappedStatement.getTimeout())
                .cache(null)
                .flushCacheRequired(false)
                .useCache(false)
                .resultOrdered(false) //?
                .keyProperty(null) //?
                .keyColumn(null) //?
                .databaseId(mappedStatement.getDatabaseId()) //?
                .resultSets(null) //?
                .build();

        RowBounds countRowBounds = (RowBounds) metaObject.getValue("delegate.rowBounds");  //?
        //ResultHandler countResultHandler = null; //?

        StatementHandler countStatementHandler =
                new RoutingStatementHandler(executor, count_ms, parameterObject, countRowBounds, null, countBoundSql);

        Connection connection = (Connection) invocation.getArgs()[0];
        Statement stmt = countStatementHandler.prepare(connection, executor.getTransaction().getTimeout());
        countStatementHandler.parameterize(stmt);
        Long total = (Long) countStatementHandler.query(stmt, null).get(0);
        page.setTotal(total);
        long pages = 0;
        if(page.getPageSize() > 0) {
            pages = total / page.getPageSize();
            if(total % page.getPageSize() != 0) pages++;
        }
        page.setPages(pages);


        //拼接limit ? ?, todo: dialect
        String sql = boundSql.getSql();
        sql += " limit ?, ?";
        metaObject.setValue("delegate.boundSql.sql", sql);

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

    private static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }


    public static String countSql(String rawSql) {
        //去除order by
        /*int idx = rawSql.lastIndexOf("order by");
        if(idx != -1 && !rawSql.substring(idx).contains(")")) {
            rawSql = rawSql.substring(0, idx);
        }*/

        //count sql
        StringTokenizer stringTokenizer = new StringTokenizer(rawSql, " \t\n\r\f(", false);
        int depth = 0;
        int selectIndex = -1;
        int fromIndex = -1;
        boolean distinct = false;

        while(stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();

            if("select".equalsIgnoreCase(token)) {
                if(depth == 0) {
                    selectIndex = rawSql.indexOf(token);
                }
                depth++;
            } else if("from".equalsIgnoreCase(token)) {
                depth--;
                if(depth == 0) {
                    fromIndex = rawSql.lastIndexOf(token);
                    break;
                }
            } else if("distinct".equalsIgnoreCase(token) && depth == 1) {
                distinct = true;
            }
        }

        if(!distinct) {
            return rawSql.substring(0, selectIndex + 6) + " count(*) " + rawSql.substring(fromIndex);
        }

        return "select count(*) from ( " + rawSql + " ) alias";
    }


}
