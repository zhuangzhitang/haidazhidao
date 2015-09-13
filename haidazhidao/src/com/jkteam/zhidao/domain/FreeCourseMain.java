package com.jkteam.zhidao.domain;
/**
 * 有课教室实体类
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class FreeCourseMain {
	private String starWeek; //起始周
	private String endWeek;  //结束周
	private String day;   //星期几
	private String coursenum; //第几大节
	private String week; //单双周
	private String className;//课室名
	
	
	
	
	public FreeCourseMain(String starWeek, String endWeek, String day,
			String coursenum, String week, String className) {
		super();
		this.starWeek = starWeek;
		this.endWeek = endWeek;
		this.day = day;
		this.coursenum = coursenum;
		this.week = week;
		this.className = className;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getStarWeek() {
		return starWeek;
	}
	public void setStarWeek(String starWeek) {
		this.starWeek = starWeek;
	}
	public String getEndWeek() {
		return endWeek;
	}
	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getCoursenum() {
		return coursenum;
	}
	public void setCoursenum(String coursenum) {
		this.coursenum = coursenum;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	@Override
	public String toString() {
		return "FreeCourseMain [starWeek=" + starWeek + ", endWeek=" + endWeek
				+ ", day=" + day + ", coursenum=" + coursenum + ", week="
				+ week + ", className=" + className + "]";
	}
	
	
}
