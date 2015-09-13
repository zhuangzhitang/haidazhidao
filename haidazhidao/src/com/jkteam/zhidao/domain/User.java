package com.jkteam.zhidao.domain;

public class User {
    private String openId;
    private String xuehao;
    private String password;
    private int status;
    private int classwarn;
    private String nickName;
    public User(){}
	public User(String openId, String xuehao, String password, int status,
			int classwarn,String nickName) {
		super();
		this.openId = openId;
		this.xuehao = xuehao;
		this.password = password;
		this.status = status;
		this.classwarn = classwarn;
		this.setNickName(nickName);
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getXuehao() {
		return xuehao;
	}
	public void setXuehao(String xuehao) {
		this.xuehao = xuehao;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getClasswarn() {
		return classwarn;
	}
	public void setClasswarn(int classwarn) {
		this.classwarn = classwarn;
	}
    @Override
    public int hashCode() {
    	// TODO Auto-generated method stub
    	return openId.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof User){
    		User user=(User)obj;
    		return this.openId.equals(user.openId);
    	}
    	return false;
    }
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
