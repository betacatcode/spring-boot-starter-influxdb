package com.github.shawn.influx.config;


import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    @Autowired
    InfluxDB influxDB;

    @Bean(name = "influxDBMapper")
    public InfluxDBMapper influxDBMapper(){
        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
        return new InfluxDBMapper(influxDB);
    }
}
