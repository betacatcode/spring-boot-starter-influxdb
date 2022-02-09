# spring-boot-starter-influxdb

## 简介

这是一个InfluxDB的框架，使用方式类似于Mybatis，对influxDB官方工具进行了简易封装，支持使用注解的方式对InfluxDB进行增、删、改操作，需配合Spring Boot使用。

## 使用方法

1.  POM文件添加依赖

   ~~~xml
   <dependency>
       <groupId>io.github.betacatcode</groupId>
       <artifactId>spring-boot-starter-influxdb</artifactId>
       <version>0.0.1-RELEASE</version>
   </dependency>
   ~~~

2. 配置yml文件

~~~yaml
spring:
  influx:
    url: http://127.0.0.1:8086
    password: 123456
    user: admin
    mapper-location: com.github.betacatcode
~~~

**其中 mapper-location 是InfluxDB Mapper存放路径** 

3. 创建表对应实体类（此处使用lombok依赖，也可不使用）

~~~java
import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;

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
~~~

4. 创建实体类对应Mapper，需继承InfluxDBBaseMapper这个接口

~~~java
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
~~~

5. 建立测试类测试

~~~java
@RunWith(SpringRunner.class)
@SpringBootTest
class InfluxdbAnnotationApplicationTests {

	@Autowired
	StudentMapper studentMapper;

	@Test
	void contextLoads() {
	}

	//单条插入
	@Test
	void testInsertOne(){
		Student student1 = new Student();
		student1.setSname("ww");
		student1.setValue(235.12);
		student1.setTime(Instant.ofEpochMilli(1640966500000l));
		studentMapper.insertOne(student1);
	}

	//批量插入
	@Test
	void testInsertBatch(){
		Student student1 = new Student();
		student1.setSname("zs");
		student1.setValue(123.45);
		student1.setTime(Instant.ofEpochMilli(1640966400000l));

		Student student2 = new Student();
		student2.setSname("ls");
		student2.setValue(666.21);
		student2.setTime(Instant.ofEpochMilli(1640966300000l));

		List<Student> studentList = new ArrayList<>();

		studentList.add(student1);
		studentList.add(student2);
		studentMapper.insertBatch(studentList);
	}

	//查询
	@Test
	void testSelect(){
		List<Student> studentList = studentMapper.selectByName("zs");
		for (Student student : studentList) {
			System.out.println(student);
		}
	}

	//删除
	@Test
	void testDelete(){
		studentMapper.deleteAll();
	}

}

~~~





