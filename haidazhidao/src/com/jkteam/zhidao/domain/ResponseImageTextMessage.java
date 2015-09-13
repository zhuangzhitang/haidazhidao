package com.jkteam.zhidao.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 响应用户的图文信息的封装类
 * @author 郭灶鹏
 *
 */
public class ResponseImageTextMessage {
	private String ToUserName;
	private String FromUserName;
	private long CreateTime;
	private String MsgType;    //这里是news
	private int ArticleCount;         //图文信息文章的数目
	private List<Articles> Articles=new ArrayList<Articles>();  //该条图文信息所包含的具体的文章集合
	public ResponseImageTextMessage(){}
	public ResponseImageTextMessage(String toUserName, String fromUserName,
			long createTime, String msgType, int articleCount,
			List<com.jkteam.zhidao.domain.Articles> articles) {
		super();
		ToUserName = toUserName;
		FromUserName = fromUserName;
		CreateTime = createTime;
		MsgType = msgType;
		ArticleCount = articleCount;
		Articles = articles;
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
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<Articles> getArticles() {
		return Articles;
	}
	public void setArticles(List<Articles> articles) {
		Articles = articles;
	}
	
	
}
