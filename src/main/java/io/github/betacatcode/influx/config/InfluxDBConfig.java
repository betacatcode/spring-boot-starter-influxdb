package io.github.betacatcode.influx.config;


import io.github.betacatcode.influx.ProxyMapperRegister;
import io.github.betacatcode.influx.core.Executor;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class InfluxDBConfig {

    @Value("${spring.influx.mapper-location}")
    private String mapperLocation;

    @Bean(name = "influxDBMapper")
    public InfluxDBMapper influxDBMapper(InfluxDB influxDB) {
        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
        return new InfluxDBMapper(influxDB);
    }

    @Bean(name = "executor")
    public Executor executor(InfluxDB influxDB, InfluxDBMapper influxDBMapper) {
        return new Executor(influxDB, influxDBMapper);
    }

    @Bean(name = "proxyMapperRegister")
    public ProxyMapperRegister proxyMapperRegister(ConfigurableApplicationContext applicationContext, ResourceLoader resourceLoader) {
        return new ProxyMapperRegister(mapperLocation, applicationContext, resourceLoader);
    }

}
