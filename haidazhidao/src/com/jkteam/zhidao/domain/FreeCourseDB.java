package com.jkteam.zhidao.domain;
/**
 * 有课表数据库操作实体类
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class FreeCourseDB {
	private int id;
    private String classname;//教室名
    private String day; //星期
    private String coursenum ; //节数
    private String week; //周数
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
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
		return "FreeCourseDB [id=" + id + ", classname=" + classname + ", day="
				+ day + ", coursenum=" + coursenum + ", week=" + week + "]";
	}
	public FreeCourseDB(String classname, String day, String coursenum,
			String week) {
		super();
		this.classname = classname;
		this.day = day;
		this.coursenum = coursenum;
		this.week = week;
	}
	public FreeCourseDB() {
		super();
	}
	
}
