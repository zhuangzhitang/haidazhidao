package com.jkteam.zhidao.domain;



/**
 * 公众号的唯一票据，一般默认两个小时内有有效
 * @author 郭灶鹏
 *
 */
public class AccessToken {
   private Integer id;         //id值
   private String accesstoken;      //获取到的凭证
   private int expires_in;            //有效时间，单位秒
   private int time;           //获取到的时间
   public AccessToken(){}
   public AccessToken(String accesstoken, int expires_in, int time) {
		super();
		this.accesstoken = accesstoken;
		this.expires_in = expires_in;
		this.time = time;
  }

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccesstoken() {
		return accesstoken;
	}
	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
   
}
