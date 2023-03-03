package io.github.betacatcode.influx.core;

import java.util.List;

/**
 * 结果集处理器
 */
public class ResultSetHandler {
    public Object handleResultSet(List<Object> list, Class returnType) {
        if (returnType == List.class) {
            return list;
        }
        if (returnType != List.class) {
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }
}
