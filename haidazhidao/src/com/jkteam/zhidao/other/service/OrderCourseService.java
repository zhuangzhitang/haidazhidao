package com.jkteam.zhidao.other.service;

import java.util.List;

import com.jkteam.zhidao.domain.OCourse;
import com.jkteam.zhidao.other.dao.OptionalCourseDao;

public class OrderCourseService {
	 private OptionalCourseDao dao=new OptionalCourseDao();
     public List<OCourse> findOpenCourse(String guanjianzi){
    	 return dao.findOpenCourse(guanjianzi);
     }
	public boolean insertOrderCourse(String openId, String courseName, int week,
			int time) {
		return dao.insertOrderCourse(openId,courseName,week,time);
		
	}
	 public int[] insertOpenAndSportCourse(){
		return  dao.insertOpenAndSportCourse();
	 }
}
