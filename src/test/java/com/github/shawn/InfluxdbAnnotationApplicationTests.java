package com.github.shawn;

import com.github.shawn.student.Student;
import com.github.shawn.student.StudentMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
