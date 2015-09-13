package com.jkteam.zhidao.domain;
/**
 * 空余教室的映射
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class ReferName {
	private String className;  //教室名
	
	private String referValue; //引用代号
	
	
	public ReferName() {
		super();
	}
	public ReferName(String className, String referValue) {
		super();
		this.className = className;
		this.referValue = referValue;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getReferValue() {
		return referValue;
	}
	public void setReferValue(String referValue) {
		this.referValue = referValue;
	}
}
