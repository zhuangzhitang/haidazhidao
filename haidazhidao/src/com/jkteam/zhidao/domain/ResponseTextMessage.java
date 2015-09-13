package com.jkteam.zhidao.domain;

/**
 * 响应用户的文本信息的封装类
 * @author 郭灶鹏
 *
 */
public class ResponseTextMessage {
	private String ToUserName;
	private String FromUserName;
	private long CreateTime;
	private String MsgType;       //这里是text类型
	private String Content;
	public ResponseTextMessage(){}
	public ResponseTextMessage(String toUserName, String fromUserName,
			long createTime, String msgType, String content) {
		super();
		ToUserName = toUserName;
		FromUserName = fromUserName;
		CreateTime = createTime;
		MsgType = msgType;
		Content = content;
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
	
}
