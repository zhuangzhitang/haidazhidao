package com.jkteam.zhidao.domain;
/**
 * 关注者实体类
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class GuangZhuUser {
  private String id;   //微信返回的用户的唯一标识
  private String nick_name;   //微信昵称
  private String remark_name;//备注
  private String group_id;//标示符
  public GuangZhuUser(){}
	public GuangZhuUser(String id, String nick_name, String remark_name,
			String group_id) {
		super();
		this.id = id;
		this.nick_name = nick_name;
		this.remark_name = remark_name;
		this.group_id = group_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getRemark_name() {
		return remark_name;
	}
	public void setRemark_name(String remark_name) {
		this.remark_name = remark_name;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
    public String toString(){
    	return "id:"+id+" nick_name:"+nick_name+"  remark_name:"+remark_name+"  group_id:"+group_id;
    }
}
