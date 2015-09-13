package com.jkteam.zhidao.domain;

public class Course {
	private int id ;
	private String openId;//外键
	private String cno;//课程名
	private String weeknum;//第几周
	private int day ; //星期几
	private int coursenum;//第几节
	private String address;//地点
	private String teacher;//老师
	
	public Course() {
		super();
	}
	
	public Course(String cno, String weeknum, int day, int coursenum,
			String address, String teacher) {
		super();
		this.cno = cno;
		this.weeknum = weeknum;
		this.day = day;
		this.coursenum = coursenum;
		this.address = address;
		this.teacher = teacher;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getCno() {
		return cno;
	}
	public void setCno(String cno) {
		this.cno = cno;
	}
	public String getWeeknum() {
		return weeknum;
	}
	public void setWeeknum(String weeknum) {
		this.weeknum = weeknum;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getCoursenum() {
		return coursenum;
	}
	public void setCoursenum(int coursenum) {
		this.coursenum = coursenum;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	@Override
	public String toString() {
		return "Course [id=" + id + ", openId=" + openId + ", cno=" + cno
				+ ", weeknum=" + weeknum + ", day=" + day + ", coursenum="
				+ coursenum + ", address=" + address + ", teacher=" + teacher
				+ "]";
	}
}
