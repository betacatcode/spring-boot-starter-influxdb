package io.github.betacatcode.influx;

import io.github.betacatcode.influx.ano.Insert;

import java.util.List;

public interface InfluxDBBaseMapper<T> {
    @Insert
    void insertOne(T entity);

    @Insert
    void insertBatch(List<T> entityList);
}
