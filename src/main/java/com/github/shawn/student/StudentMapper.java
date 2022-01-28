package com.github.shawn.student;

import com.github.shawn.influx.InfluxDBBaseMapper;
import com.github.shawn.influx.ano.Delete;
import com.github.shawn.influx.ano.Insert;
import com.github.shawn.influx.ano.Select;

import java.util.List;

public interface StudentMapper extends InfluxDBBaseMapper {

    @Select(value = "select * from test.autogen.student where sname=#{sname}",resultType = Student.class)
    List<Student> selectByName(String sname);

    @Delete(value = "delete from student",database = "test")
    void deleteAll();

    @Insert
    void insertOne(Student student);

    @Insert
    void insertBatch(List<Student> students);


}
