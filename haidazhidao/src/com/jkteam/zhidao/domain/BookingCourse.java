package com.jkteam.zhidao.domain;
/**
 * 预定选修课实体类
 * @author ZheTang
 * @date 2015-4-25
 *
 */
public class BookingCourse {
  private int id;   //主键id 
  private String  openId; //微信唯一标示号
  private int cid;  //外键，关联optionalcourse表
  private int day;   //星期几，0代表星期日
  private int coursenum; //第几大节，1代表第1,2小节，2代表第3,4小节,3代表5,6大节
  private boolean issuccess; //是否预定成功，0为失败，1为成功
  public BookingCourse(){}
	public BookingCourse(String openId, int cid, int day, int coursenum,
			boolean issuccess) {
		super();
		this.openId = openId;
		this.cid = cid;
		this.day = day;
		this.coursenum = coursenum;
		this.issuccess = issuccess;
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
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
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
	public boolean isIssuccess() {
		return issuccess;
	}
	public void setIssuccess(boolean issuccess) {
		this.issuccess = issuccess;
	}
	  
  
}
