package com.collaboportal.common.database.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;
import java.util.Map;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class AuditInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args != null && args.length > 1 && args[1] != null) {
            Object param = args[1];
            Object target = param;
            if (param instanceof Map<?, ?> map) {
                Object p1 = map.get("param1");
                if (p1 != null) target = p1;
            }
            setIfPresent(target, "updatedTime", new Date());
            if (isInsert(invocation.getArgs()[0])) {
                setIfPresent(target, "createdTime", new Date());
            }
        }
        return invocation.proceed();
    }

    private boolean isInsert(Object ms) {
        if (ms instanceof MappedStatement m) {
            String id = m.getId();
            return id != null && id.toLowerCase().contains("insert");
        }
        return false;
    }

    private void setIfPresent(Object target, String fieldName, Object value) {
        if (target == null) return;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
