package com.jkteam.zhidao.domain;
/**
 * 考试安排实体类
 * @author ZheTang
 * @date 2015-5-11
 *
 */
public class Exam {
	private int examid;
	private String examName; //考试名
	private String examTime; //考试时间
	private String openId;
	
	public Exam() {
		super();
	}
	public Exam(int examid, String examName, String examTime, String openId) {
		super();
		this.examid = examid;
		this.examName = examName;
		this.examTime = examTime;
		this.openId = openId;
	}
	public int getExamid() {
		return examid;
	}
	public void setExamid(int examid) {
		this.examid = examid;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	@Override
	public String toString() {
		return "Exam [examid=" + examid + ", examName=" + examName
				+ ", examTime=" + examTime + ", openId=" + openId + "]";
	}
	
}
