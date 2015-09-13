package com.jkteam.zhidao.test;

import java.util.ArrayList;
import java.util.List;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.query.dao.CourseDao;
import com.jkteam.zhidao.util.FreeCourse;

/**
 * 空余教室测试类
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class Demo {
	public static void main(String[] args) {
	
	   CourseDao dao = new CourseDao();
	   List<Course> list = dao.getCoursesOnCurrentTime(4, 9);
		FreeCourse free = new FreeCourse(list);
		free.getZhulouFreeCourse();
		List<String> zhulouFree = free.getZhulouFreeCourse();
		System.out.println("主楼空闲教室------------------");
		for(String name:zhulouFree){
			System.out.println(name);
		}
		System.out.println("主楼空闲教室------------------");
		
		List<String> zhonghailou = free.getZhonghailouFreeCourse();
	    System.out.println("----------钟海楼-空余教室--------------");
	    for(String name:zhonghailou){
	    	System.out.println(name);
	    }
	    System.out.println("----------钟海楼-------空余教室--------");
	}
	
	
}
