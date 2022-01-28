package com.github.shawn.student;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Data
@Measurement(database = "test",name = "student")
public class Student {
    private String id;

    @Column(name = "sname",tag = true)
    private String sname;

    @Column(name = "value")
    private Double value;

    @TimeColumn
    @Column(name = "time")
    private Instant time;
}
