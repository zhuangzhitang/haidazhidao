package com.jkteam.zhidao.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.util.CreeperInfos;

/**
 * 课程表数据整合
 * @author ZheTang
 * @date 2015-5-17
 *
 */
public class Test {
     public static void main(String[] args) {
		CreeperInfos infos = new CreeperInfos("201211621133", "zhetang6360182");
		List<Course> courses = infos.getStudentCource();
		Map<Integer,Map<Integer,List<Course>>> map = new HashMap<Integer,Map<Integer,List<Course>>>();
		for(Course course:courses){
			int  xkey = course.getCoursenum();
			Map<Integer, List<Course>> ymap =null; ;
			if(map.containsKey(xkey)){
				int ykey = course.getDay();//星期几 即纵坐标
				ymap = map.get(course.getCoursenum());//获取第一节的map集合
				if(ymap.containsKey(ykey)){
					List<Course> list = ymap.get(ykey);
					list.add(course);
					ymap.put(ykey, list);
					
				}else{
					List<Course> list = new ArrayList<Course>();
					list.add(course);
					ymap.put(ykey, list);
				}
				map.put(xkey, ymap);
			}else{
				ymap  = new HashMap<Integer, List<Course>>();
				List<Course> list = new ArrayList<Course>();
				list.add(course);
				ymap.put(course.getDay(),list);
				map.put(xkey, ymap);
			}
		}
		
		int i = 0;
		for(Map.Entry<Integer,Map<Integer,List<Course>>>  temp: map.entrySet()){
			i++;
			System.out.print("第"+i+"节");
			Map<Integer, List<Course>> couseMap = temp.getValue();
			for(Map.Entry<Integer, List<Course>> entry:couseMap.entrySet()){
				List<Course> course = entry.getValue();
				Course cr = course.get(0);
				//if(cr.getDay())
				for(Course c : course){
					System.out.print("星期 ："+ c.getDay()+ c.getCno()+"--"+ c.getWeeknum()+"  ");
				}
				System.out.print("|");
			}
			System.out.println("------");
		}
	}
}
