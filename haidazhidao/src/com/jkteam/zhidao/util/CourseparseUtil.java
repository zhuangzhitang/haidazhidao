package com.jkteam.zhidao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jkteam.zhidao.domain.Course;
/**
 * 课表展示时，需要调整的数据
 * @author ZheTang
 * @date 2015-5-17
 *
 */
public class CourseparseUtil {
	public static Map<Integer,Map<Integer,List<Course>>> toCourse(List<Course> courses){
		//Map<Integer,Map<Integer,List<Course>>> ：分别 指：xkey,ykey,第几节，星期几对应的课程
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
		return  map;
	}
}
