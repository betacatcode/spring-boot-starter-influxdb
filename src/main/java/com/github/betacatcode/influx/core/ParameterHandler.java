package com.github.betacatcode.influx.core;

import java.lang.reflect.Parameter;

/**
 * 参数处理器
 */
public class ParameterHandler {

    /**
     * 拼接sql
     * @param parameters 参数名
     * @param args 参数实际值
     * @param sql
     * @return
     */
    public String handleParameter(Parameter[] parameters,Object[] args,String sql){
        for(int i=0;i<parameters.length;i++){
            Class<?> parameterType=parameters[i].getType();
            String parameterName=parameters[i].getName();

            if(parameterType==String.class){
                sql=sql.replaceAll("\\#\\{"+parameterName+"\\}","'"+args[i]+"'");
                sql=sql.replaceAll("\\$\\{"+parameterName+"\\}",args[i].toString());
            }else {
                sql=sql.replaceAll("\\#\\{"+parameterName+"\\}",args[i].toString());
                sql=sql.replaceAll("\\$\\{"+parameterName+"\\}",args[i].toString());
            }
        }
        return sql;
    }
}
