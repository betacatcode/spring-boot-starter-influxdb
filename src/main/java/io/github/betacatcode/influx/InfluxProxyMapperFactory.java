package io.github.betacatcode.influx;

import io.github.betacatcode.influx.core.Executor;
import io.github.betacatcode.influx.core.ParameterHandler;
import io.github.betacatcode.influx.core.ResultSetHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class InfluxProxyMapperFactory<T> implements FactoryBean {

    @Autowired
    Executor executor;

    private Class<T> interfaceClass;

    public InfluxProxyMapperFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object getObject() throws Exception {
        Object proxyInstance = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new ProxyMapper(new ParameterHandler(),executor,new ResultSetHandler()));
        return proxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
