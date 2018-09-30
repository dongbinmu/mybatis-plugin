package com.weaponlin.mybatis.plugin;

import com.weaponlin.mybatis.plugin.strategy.ShardingStrategy;
import com.weaponlin.mybatis.plugin.util.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.sql.Connection;
import java.util.Properties;

@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ShardingInterceptor implements Interceptor {

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    private ShardingStrategy shardingStrategy;

    /**
     * TODO
     */
    private ShardingProperties shardingProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // save session, why?
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler,
                DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);

        // get sql command type
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (sqlCommandType != null && sqlCommandType != SqlCommandType.UNKNOWN && sqlCommandType != SqlCommandType.FLUSH) {

            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            // rest sql
            String newSql = getSql(boundSql, sqlCommandType);
            metaStatementHandler.setValue("delegate.boundSql.sql", newSql);
        }

        return invocation.proceed();
    }

    /**
     * 获取分表后的sql
     */
    private String getSql(BoundSql boundSql, SqlCommandType sqlCommandType) {
        // 0、获取sql
        String sql = boundSql.getSql().trim().toLowerCase();
        log.info("sql: {}", sql);
        // 1. get database and table
        Pair<String, String> databaseAndTable = SQLUtil.getDatabaseAndTable(sql, sqlCommandType);
        if (databaseAndTable == null || !shardingProperties.isValidDatabaseAndTable(databaseAndTable)) {
            return sql;
        }

        // 2. sharding database and table with sharding strategy
        Object hash = ((MapperMethod.ParamMap) boundSql.getParameterObject()).get("hash");
        String completeTable = shardingStrategy.completeTable(databaseAndTable.getLeft(), databaseAndTable.getRight(),
                shardingProperties.getDatabaseSize(), shardingProperties.getTableSize(), (Long) hash);

        // 3. replace old table with new table
        String oldTable = " " + databaseAndTable.getLeft() + "." + databaseAndTable.getRight();
        String newTable = " " + completeTable + " ";
        String newSql = sql.replace(oldTable, newTable);
        return newSql;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(final Properties properties) {
        shardingProperties = ShardingProperties.fromProperties(properties);
        try {
            Class<?> clazz = Class.forName(shardingProperties.getShardingStrategy());
            shardingStrategy = (ShardingStrategy) clazz.newInstance();
        } catch (Exception e) {
            log.error("error occurred when set properties", e);
            throw new RuntimeException("initialize sharding strategy ");
        }
    }
}
