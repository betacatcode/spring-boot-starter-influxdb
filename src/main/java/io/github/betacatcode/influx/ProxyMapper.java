package io.github.betacatcode.influx;

import io.github.betacatcode.influx.ano.Delete;
import io.github.betacatcode.influx.ano.Insert;
import io.github.betacatcode.influx.ano.Select;
import io.github.betacatcode.influx.core.Executor;
import io.github.betacatcode.influx.core.ParameterHandler;
import io.github.betacatcode.influx.core.ResultSetHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class ProxyMapper implements InvocationHandler {

    private ParameterHandler parameterHandler;
    private Executor executor;
    private ResultSetHandler resultSetHandler;

    public ProxyMapper(ParameterHandler parameterHandler,Executor executor,ResultSetHandler resultSetHandler) {
        this.parameterHandler = parameterHandler;
        this.executor = executor;
        this.resultSetHandler = resultSetHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation[] annotations = method.getAnnotations();
        if(annotations.length==1){
            Annotation annotation = annotations[0];
            Class<? extends Annotation> annotationType = annotation.annotationType();
            //是查询的
            if(annotationType==Select.class){
                Select selectAnnotation = method.getAnnotation(Select.class);
                //拼接sql
                String sql = selectAnnotation.value();
                Parameter[] parameters = method.getParameters();
                sql = parameterHandler.handleParameter(parameters,args,sql);
                //查询结果
                Class resultType = selectAnnotation.resultType();
                List<Object> list = executor.select(sql, resultType);
                //根据返回类型返回结果
                Class<?> returnType = method.getReturnType();
                return resultSetHandler.handleResultSet(list,returnType);
            }
            //是插入的
            if(annotationType== Insert.class){
                executor.insert(args);
            }
            //是删除的
            if(annotationType== Delete.class){
                Delete deleteAnnotation = method.getAnnotation(Delete.class);
                //拼接sql
                String sql = deleteAnnotation.value();
                Parameter[] parameters = method.getParameters();

                String database = deleteAnnotation.database();
                sql = parameterHandler.handleParameter(parameters,args,sql);
                //执行sql
                executor.delete(sql,database);
            }
        }
        return null;
    }
}
