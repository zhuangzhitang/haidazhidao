package com.jkteam.zhidao.domain;
/**
 * 校公选课实体类
 * @author ZheTang
 * @date 2015-4-25
 *
 */
public class OCourse {
    private int id;
    private String name;
    private int type;
    public OCourse(){}
	public OCourse(int id, String name, int type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
    
}
