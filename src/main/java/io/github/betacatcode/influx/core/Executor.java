package io.github.betacatcode.influx.core;

import org.influxdb.InfluxDB;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.impl.InfluxDBMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行器
 */
public class Executor {
    InfluxDB influxDB;
    InfluxDBMapper influxDBMapper;

    public Executor() {
    }

    public Executor(InfluxDB influxDB, InfluxDBMapper influxDBMapper) {
        this.influxDB = influxDB;
        this.influxDBMapper = influxDBMapper;
    }

    public <E> List<E> select(String sql, Class domainClass){
        List<E> results = influxDBMapper.query(new Query(sql), domainClass);
        return results;
    }

    public void insert(Object args[]){
        if(args.length!=1){
            throw new RuntimeException();
        }
        Object obj = args[0];
        //插入的是集合类型的
        if(obj instanceof List){
            List list = (ArrayList)obj;
            if(list.size()>0){
                Object firstObj = list.get(0);
                Class<?> domainClass = firstObj.getClass();
                List<Point> pointList = new ArrayList<>();
                for (Object o : list) {
                    Point point = Point
                            .measurementByPOJO(domainClass)
                            .addFieldsFromPOJO(o)
                            .build();
                    pointList.add(point);
                }

                //获取数据库名和rp
                Measurement measurement = firstObj.getClass().getAnnotation(Measurement.class);
                String database = measurement.database();
                String retentionPolicy = measurement.retentionPolicy();
                BatchPoints batchPoints = BatchPoints
                                                    .builder()
                                                    .points(pointList)
                                                    .retentionPolicy(retentionPolicy).build();
                influxDB.setDatabase(database);
                influxDB.write(batchPoints);
            }

            //插入单个
        }else {
            influxDBMapper.save(obj);
        }
    }

    public void delete(String sql,String database){
        influxDB.query(new Query(sql,database));
    }

}
