package com.jkteam.zhidao.base.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jkteam.zhidao.base.dao.UserDao;
import com.jkteam.zhidao.domain.Articles;
import com.jkteam.zhidao.domain.ResponseImageTextMessage;
import com.jkteam.zhidao.domain.ResponseTextMessage;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.query.dao.CourseDao;
import com.jkteam.zhidao.util.DataSafeModeUtil;
import com.jkteam.zhidao.util.MessageToXmlUtil;
import com.jkteam.zhidao.util.SendMessageUtil;
import com.jkteam.zhidao.util.URLUtil;
import com.jkteam.zhidao.util.XiuGaiBeiZhuUtil;

/**
 * 整个微信公众号设计的核心类，也是提供有关User表的service方法
 * @author 郭灶鹏
 *
 */
public class UserService {
	private Logger log=LoggerFactory.getLogger(UserService.class);
	//微信号名称
	public static final String WEIXIN_HAO="alading-378";
	private UserDao useDao=new UserDao();
	private CourseDao courseDao = new CourseDao();
	/**
	 * 回复文本信息，并以xml格式写出数据
	 * @param text   响应文本信息的封装对象
	 * @param response
	 */
	public void replyTextMessage(ResponseTextMessage text,HttpServletResponse response) {
		String xml=MessageToXmlUtil.textMessageToXml(text);
		String jiami=DataSafeModeUtil.encryptData(xml);
		try {
			response.getWriter().print(jiami);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	/**
	 * 回复图文信息
	 * @param newsMessage 响应图文信息的封装对象
	 * @param response
	 */
	public void replyImageTextMessage(ResponseImageTextMessage newsMessage,HttpServletResponse response){
		String xml=MessageToXmlUtil.newsMessageToXml(newsMessage);
		String jiami=DataSafeModeUtil.encryptData(xml);
		try {
			response.getWriter().print(jiami);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	/**
	 * 根据用户发出的请求信息，作出相应的回复，被processMesssageFromWeixin（）方法调用。
	 * @param content 用户的请求信息
	 * @param FromUserName  微信用户的openID值
	 * @param response 
	 */
	private void reply(String content,String FromUserName,HttpServletResponse response){
		Map<String,Integer> connection=new HashMap<String,Integer>();
		connection.put("绑定",1);
		connection.put("解除绑定",2);
		connection.put("开提醒",3);
		connection.put("关提醒",4);
		connection.put("成绩",5);
		connection.put("课表",6);
		connection.put("自习教室",7);
		connection.put("公共选课",8);
		connection.put("教务通知",9);
		connection.put("预订选修",10);
		connection.put("等级考试",11);
		connection.put("考试安排",12);
		connection.put("后台管理",13);
		Integer messageId=null;
		if(content.matches("[0-9]{1,2}")){
			messageId=Integer.parseInt(content);
		}else{
		    messageId=connection.get(content);
		}
		long createtime=new Date().getTime();
		if(useDao.isbinding(FromUserName)){
		  ResponseTextMessage text=null;
		  ResponseImageTextMessage news=null;
		  List<Articles> articles=new ArrayList<Articles>();
		  Articles a=null;
		  String link=null;
		  if(messageId!=null){
			  switch (messageId) {
			   case 1:
				  text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","对不起，您的微信号已经绑定成功，您可以选择其他功能");
				  this.replyTextMessage(text, response);
			      break;
			   case 2:
				  if(useDao.removeBinding(FromUserName)==1&&courseDao.deleteCourseByOpendId(FromUserName)>0){
					 text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","解绑成功");
				  }else{
					 text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","解绑失败，请重新尝试");
				  }
				  this.replyTextMessage(text, response);
			      break;
			   case 3:
				    if(useDao.isclassWarn(FromUserName)){
				    	text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒已经开启");
				    }else{
				    	if(useDao.openOrCloseClassWarn(FromUserName,1)==1){
				    	   text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒开启成功");
				    	}else{
				    	   text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒开启失败，请重新尝试");
				    	}
				    }
				    this.replyTextMessage(text, response);
				    break;
			   case 4:
				   if(useDao.isclassWarn(FromUserName)){
					   if(useDao.openOrCloseClassWarn(FromUserName,0)==1){
				    	   text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒关闭成功");
				    	}else{
				    	   text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒关闭失败，请重新尝试");
				    	}
				   }else{
					   text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","上课提醒没有开启，无需关闭。");
				   }
				   this.replyTextMessage(text, response);
				    break;
			   case 5:
				    a=new Articles("成绩查询结果","请点击查看详细的成绩报告",null,URLUtil.find_score_url.replace("OPENID",FromUserName));
				    articles.add(a);
				    news=new ResponseImageTextMessage(FromUserName,WEIXIN_HAO, createtime,"news",1, articles);
				    this.replyImageTextMessage(news, response);
				    break;
			   case 6:
				    a=new Articles("课表查询结果","请点击查看详细的课表",null,URLUtil.find_class_url.replace("OPENID",FromUserName));
				    articles.add(a);
				    news=new ResponseImageTextMessage(FromUserName,WEIXIN_HAO, createtime,"news",1, articles);
				    this.replyImageTextMessage(news, response);
				    break;
			   case 7:
				    link="<a href='"+URLUtil.find_classroom_url+"'>点我,查询自习教室</a>";
				    text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
				    break;
			   case 8:
				    link="<a href='"+URLUtil.find_yibaoxuanxiu_url.replace("OPENID",FromUserName)+"'>点我,公共选课查询</a>";
				    text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
				    break;
			   case 9:
				    a=new Articles("教务通知查询结果","请点击查看详细的教务系统通知",null,URLUtil.find_jiaowutongzhi_url.replace("OPENID",FromUserName));
				    articles.add(a);
				    news=new ResponseImageTextMessage(FromUserName,WEIXIN_HAO, createtime,"news",1, articles);
				    this.replyImageTextMessage(news, response);
				    break;
			   case 10:
				    a=new Articles("预订选修课","请点击查看预订选修课的细节说明",null,URLUtil.order_xuanxiuke_url.replace("OPENID",FromUserName));
				    articles.add(a);
				    news=new ResponseImageTextMessage(FromUserName,WEIXIN_HAO, createtime,"news",1, articles);
				    this.replyImageTextMessage(news, response);
				    break;
			   case 11:
				    link="<a href='"+URLUtil.baoming_kaoshi_url.replace("OPENID",FromUserName)+"'>点我,进入等级考试报名</a>";
				    text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
				    break;
			   case 12:
				    link="<a href='"+URLUtil.EXAM_QUERY_URL_STRING.replace("OPENID",FromUserName)+"'>点我,进入考试安排查询</a>";
				    text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
				    break;
			   case 13:
				    link="<a href='"+URLUtil.HOUTAI_GUANLI_URL+"'>点我,进入后台管理</a>";
				    text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
				    break;
			   default:
				   this.defaultReply(FromUserName, response);
				   break;
		  }
		  }else if(messageId==null){
        	  this.defaultReply(FromUserName, response);
		  }
		
		}else{
			if(messageId!=null){
				switch (messageId) {
				case 1:
					String link="<a href='"+URLUtil.binding_user_url.replace("OPENID",FromUserName)+"'>点我,进入账号绑定</a>";
				    ResponseTextMessage text=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text", link);
				    this.replyTextMessage(text, response);
					break;
				default:
					ResponseTextMessage text1=new ResponseTextMessage(FromUserName,WEIXIN_HAO, createtime,"text","您尚未绑定账号，无法使用其他功能，请先绑定账号");
					this.replyTextMessage(text1, response);
					break;
			     }
			}else if(messageId==null){
		     	  this.defaultReply(FromUserName, response);
			}
		}
	}
	/**
	 * 处理发自微信用户的加密信息，进行解密，并作出作出相应的响应。
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void processMesssageFromWeixin(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		InputStream input=request.getInputStream();
		   InputStreamReader reader=new InputStreamReader(input,"UTF-8");
		   BufferedReader bufferReader=new BufferedReader(reader);
	       String s=null;
	       StringBuffer buf=new StringBuffer();
	       while((s=bufferReader.readLine())!=null){
	    	   buf.append(s);
	       }
	       String msg=buf.toString();
	       input.close();
	       String msgSignature=request.getParameter("msg_signature");
	       String timestamp=request.getParameter("timestamp");
	       String nonce=request.getParameter("nonce");
	     /* System.out.println("msgSignature:  "+msgSignature);
	       System.out.println("timestamp:  "+timestamp);
	       System.out.println("msg:  "+msg);*/
	       String jiemi=DataSafeModeUtil.decryptData(msg, msgSignature, timestamp, nonce);
	       try {
			  Document document=DocumentHelper.parseText(jiemi);
	    	  // SAXReader sax=new SAXReader();
	    	   //Document document=sax.read(input);
			 Element element=document.getRootElement();
			 String FromUserName=element.elementText("FromUserName");
			 String MsgType=element.elementText("MsgType");
			 if(MsgType.equals("text")){
			    String Content=element.elementText("Content");
			    this.reply(Content, FromUserName, response);
			 }else if(MsgType.equals("event")){
				 String eventType=element.elementText("Event");
				 //取消订阅
				 if(eventType.equals("unsubscribe")){
					 //取消绑定
					  useDao.removeBinding(FromUserName);
				 }else if(eventType.equals("subscribe")){
					 String id=SendMessageUtil.getAllUser().get(0).getId();
					 XiuGaiBeiZhuUtil.xiugaibeizhu(id, FromUserName);
					 this.defaultReply(FromUserName, response);
				 }
			 }
			 else{
				this.defaultReply(FromUserName, response);
			 }
		   } catch (DocumentException e) {
			  log.error(e.getMessage());
		  }
	}
	/**
	 * 默认回复
	 * @param toUserName 回复的用户openId
	 * @param response
	 */
	private void defaultReply(String toUserName,HttpServletResponse response){
		System.out.println("-------------默认回复-------------");
		String neirong="欢迎关注海大知道！ \n\n"+
                       "1.账号绑定【绑定】\n"+
		               "2.解除账号绑定【解除绑定】\n"+
		               "3.开启上课提醒【开提醒】\n"+
		               "4.关闭上课提醒【关提醒】\n"+
		               "5.成绩查询【成绩】\n"+
		               "6.课表查询【课表】\n"+
		               "7.自习教室查询【自习教室】\n"+
		               "8.公共选课查询【公共选课】\n"+
		               "9.教务通知查询【教务通知】\n"+
		               "10.预订选修课【预订选修】\n"+
		               "11.等级考试报名【等级考试】\n"+
		               "12.考试安排查询【考试安排】\n"+
		               "回复【】内的关键字或者数字，获取相关服务，\n"+
		               "回复其他文字重复以上菜单。";
	     long createtime=new Date().getTime();
         ResponseTextMessage text=new ResponseTextMessage(toUserName,WEIXIN_HAO, createtime,"text",neirong);
         this.replyTextMessage(text, response);
	}
	/**
	 * 绑定用户
	 * @param user
	 * @return
	 */
	public int bindingUser(User user){
		if(useDao.queryUser(user.getOpenId())!=null){
			return useDao.updateUserStatus(user.getOpenId());
		}else{
			return useDao.insertUser(user);
		}
	}
	/**
	 * 判断用户是否已经绑定
	 * @param openId
	 * @return
	 */
	public boolean isbinding(String openId){
		return useDao.isbinding(openId);
	}
	
	//通过openId 获取user
	public User queryUser(String openId){
		return useDao.queryUser(openId);
	}
	
	public List<User> queryUser(){
		return useDao.queryUser();
	 }
	/**
	 * 后台登录
	 * @param name 用户名
	 * @param pass 密码
	 * @return
	 */
	 public boolean loginHoutai(String name,String pass){
	    	return useDao.loginHoutai(name, pass);
	    }
}
