package com.jkteam.zhidao.query.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.query.dao.CourseDao;
import com.jkteam.zhidao.util.DateUtil;
import com.jkteam.zhidao.util.FreeCourse;
/**
 * 空余课室业务操作类
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class CourseService {
	private Logger log=LoggerFactory.getLogger(UserService.class);
	private CourseDao dao = new CourseDao();
	
	public List<Course> queryByOpenId(String openId){
		return dao.queryByOpenId(openId);
	}
	
	public int deleteAllCourse(){
		return dao.deleteAllCourse();
	}
	public int insertUser(List<Course> courses,String openId){
		int flag = 0;
		for (int i = 0; i < courses.size(); i++) {
			Course course = courses.get(i);
			course.setOpenId(openId);
			flag = dao.insertUser(course);
		}
		return flag ;
	}
	
	/**
	 * 返回教室名
	 * 
	 * @return  Map<Integer, List<String>>    key为1,：表示 主楼的空余教室  key 为2 :表示钟海楼的空余教室
	 */
	public Map<Integer, List<String>> getFreeCourse(){
		Date date = new Date();
		int weekDay = DateUtil.getWeekDay(date);//获取星期
		int coursenum = DateUtil.getCoursenum(date);//获取第几节
		//System.out.println(weekDay+"----------------"+coursenum);
		List<Course> courses = dao.getCoursesOnCurrentTime(weekDay,coursenum);
		Map<Integer, List<String>> map = null;
		if(courses !=null){
			FreeCourse freeCourse = new FreeCourse(courses);
		    map = new HashMap<Integer, List<String>>();
		    map.put(1, freeCourse.getZhulouFreeCourse());
		    map.put(2, freeCourse.getZhonghailouFreeCourse());
		}
		
		return map;
	}
	public int  deleteCourseByOpendId(String opendId){
		return dao.deleteCourseByOpendId(opendId);
	}
}
