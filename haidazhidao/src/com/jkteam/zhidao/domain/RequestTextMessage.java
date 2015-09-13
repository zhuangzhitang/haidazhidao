package com.jkteam.zhidao.domain;

/**
 * 来自用户的文本信息的封装类
 * @author 郭灶鹏
 *
 */
public class RequestTextMessage {
   private String ToUserName;    //微信订阅号名称
   private String FromUserName;  //微信用户的OpenID
   private long CreateTime;     //创建时间
   private String MsgType;     //信息的类型，这里是text
   private String Content;    //信息内容
   private long MsgId; 
   public RequestTextMessage(){}
   public RequestTextMessage(String toUserName, String fromUserName,
			long createTime, String msgType, String content, long msgId) {
		super();
		ToUserName = toUserName;
		FromUserName = fromUserName;
		CreateTime = createTime;
		MsgType = msgType;
		Content = content;
		MsgId = msgId;
	}
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public long getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public long getMsgId() {
		return MsgId;
	}
	public void setMsgId(long msgId) {
		MsgId = msgId;
	}
	   
}
