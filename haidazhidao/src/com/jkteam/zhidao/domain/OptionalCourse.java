package com.jkteam.zhidao.domain;
//选修课
public class OptionalCourse {
	private String optioncName;//选修课名
	private String teacher;//教师
	private String time;//上课时间
	private String address;//上课地点
	
	public OptionalCourse() {
		super();
	}
	
	public OptionalCourse(String optioncName, String teacher, String time,
			String address) {
		super();
		this.optioncName = optioncName;
		this.teacher = teacher;
		this.time = time;
		this.address = address;
	}

	public String getOptioncName() {
		return optioncName;
	}
	public void setOptioncName(String optioncName) {
		this.optioncName = optioncName;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "OptionalCourse [optioncName=" + optioncName + ", teacher="
				+ teacher + ", time=" + time + ", address=" + address + "]";
	}

	
	
}
