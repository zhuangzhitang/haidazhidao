package com.jkteam.zhidao.test;

import java.util.List;

import org.junit.Test;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.query.dao.CourseDao;

public class CourseDaoTest {
	CourseDao dao = new CourseDao();
	@Test
	public void testDeleteCourseByOpendId() {
		int i =dao.deleteCourseByOpendId("orfG4jn9-kif3l1v_TXf6gxZKrcg");
		System.out.println(i);
	}
	@Test
	public void testGetCoursesOnCurrentTime() {
		List<Course> list =  dao.getCoursesOnCurrentTime(4, 9);
		
		for(Course c:list){
			System.out.println(c.getCno()+" : "+c.getAddress());
		}
	}
	@Test 
	public void testInsert(){
		
		for(int i =1 ; i <26;i++){
			int j = 300+i;
			
			Course course = new Course("121", "fasf", 4, 9, "主楼"+j, "fsa");
			course.setOpenId("123");
			dao.insertUser(course);
		}
		
	}

}
