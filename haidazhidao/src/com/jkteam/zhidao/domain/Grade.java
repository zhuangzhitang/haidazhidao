package com.jkteam.zhidao.domain;

public class Grade {
	private String year;   //学年
	private String term;   //学期
	private String courseName;  //课程名
	private String courseType;  //课程属性
	private String credit;    //学分
	private String gradePoint;//绩点
	private String score;  //成绩
	
	public Grade() {
		super();
	}
	public Grade(String year, String term, String courseName,
			String courseType, String credit, String gradePoint, String score) {
		super();
		this.year = year;
		this.term = term;
		this.courseName = courseName;
		this.courseType = courseType;
		this.credit = credit;
		this.gradePoint = gradePoint;
		this.score = score;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getGradePoint() {
		return gradePoint;
	}
	public void setGradePoint(String gradePoint) {
		this.gradePoint = gradePoint;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
}
