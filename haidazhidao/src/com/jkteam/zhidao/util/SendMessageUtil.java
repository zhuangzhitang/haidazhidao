package com.jkteam.zhidao.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.GuangZhuUser;
import com.jkteam.zhidao.domain.MyX509TrustManager;




public class SendMessageUtil { 
	private static UserService userService=new UserService();
	
	public static void main(String[] args) {
		//System.out.println(isSuccess("604635841","测试中"));
		String id=SendMessageUtil.getid("oXwlNuJrGmyQVzThhPRDegQc4bnA");
		SendMessageUtil.isSuccess(id,"ok");
	}
	
	public static String getid(String openId){
		String nickName=userService.queryUser(openId).getNickName();
		for(GuangZhuUser u:SendMessageUtil.getAllUser()){
			if(u.getNick_name().equals(nickName)){
				return u.getId();	
			}
		}
		return "";
	}
   public static boolean isSuccess(String id,String content){
	   Map<String,String> messageMap=httpRequest("https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN","POST","username=995006199@qq.com&pwd=f01deddcea4d414bc5a4235db76ec6da");
	   String data="token=TOKEN&lang=zh_CN&f=json&ajax=1&random:0.05862049013376236&type=1&content=NEIRONG&tofakeid=PERSONID&imgcode=";
	   String newdata=data.replace("NEIRONG",content).replace("PERSONID",id).replace("TOKEN",messageMap.get("token"));
	   String u="https://mp.weixin.qq.com/cgi-bin/singlesend?t=ajax-response&f=json&token=TOKEN&lang=zh_CN";
	   String huidiao=sendMessage(u.replace("TOKEN",messageMap.get("token")),"POST",newdata,messageMap);
	   System.out.println(huidiao);
	   if(JSONObject.fromObject(JSONObject.fromObject(huidiao).getString("base_resp")).getString("err_msg").equals("ok")){
		    return true;
	   }else{
		   return false;
	   }
   }
   public static String sendMessage(String requesturl,String requestMethod,String outputStr,Map<String,String> messageMap){
	   StringBuffer buf=new StringBuffer();
	   try {
		   String cookie="remember_acct=995006199%40qq.com; ssuid=7912378624;"+
	                    " ts_refer=jingyan.baidu.com/article/6525d4b134051eac7d2e9417.html; ts_uid=6102891860;"+
				        " mpuv=CqOGCFVG8OKHF2TcGXjRAg==; ptui_loginuin=995006199@qq.com;"+
	                    " ptcz=24296f219c12d255a6580d695ee381c2b6505340c90f633043950999631ac0c7; pt2gguin=o0995006199;"+
				        " pgv_pvid=5770065052; o_cookie=995006199; pgv_pvi=947729408; data_bizuin=3003524479;"+"" +
				        " data_ticket=DATA_TICKET; slave_user=SLAVE_USER;"+
				        " slave_sid=SLAVE_SID; bizuin=3091820797";
		   cookie=cookie.replace("DATA_TICKET",messageMap.get("data_ticket")).replace("SLAVE_USER",messageMap.get("slave_user")).replace("SLAVE_SID",messageMap.get("slave_sid"));
		   TrustManager[] tm={(TrustManager) new MyX509TrustManager()};
		   SSLContext sslContext=SSLContext.getInstance("SSL","SunJSSE");
		   sslContext.init(null,tm, new java.security.SecureRandom());
		   SSLSocketFactory ssf=sslContext.getSocketFactory();
		   URL url=new URL(requesturl);
		   HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
		   conn.setSSLSocketFactory(ssf);
		   conn.setDoOutput(true);
		   conn.setDoInput(true);
		   conn.setUseCaches(false);
		   conn.setConnectTimeout(8000);
		   conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		   String referer="https://mp.weixin.qq.com/cgi-bin/singlesendpage?t=message/send&action=index&tofakeid=604635841&token=TOKEN&lang=zh_CN";
		   conn.setRequestProperty("Referer",referer.replace("TOKEN",messageMap.get("token")));
		   conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
		   conn.setRequestProperty("Host","mp.weixin.qq.com");
		   conn.setRequestProperty("Cookie",cookie);
		   
		   conn.setRequestMethod(requestMethod);
		   conn.connect();
		   if(outputStr!=null){
			  OutputStream output=conn.getOutputStream();
			  output.write(outputStr.getBytes("UTF-8"));
			  output.close();
		   }
		   String line=null;
		   InputStream input=conn.getInputStream();
		   BufferedReader bufferStream=new BufferedReader(new InputStreamReader(input,"UTF-8"));
		   while((line=bufferStream.readLine())!=null){
			   buf.append(line);
		   }
		  input.close();
		  bufferStream.close();
		  conn.disconnect();
		  return buf.toString();
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
   }
   
  
   
   public static List<GuangZhuUser> getAllUser(){
	     String c=httpRequest3("https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=0&type=0&token=TOKEN","GET",null);
		 String json=jsouphtml(c);
		 String buf=json.substring(json.indexOf("=")+1);
		 String users=buf.substring(buf.indexOf("contacts")+11,buf.lastIndexOf("contacts")-4);
		 String[] ss=users.split("},");
		 List<JSONObject> userlist=new ArrayList<JSONObject>();
		 for(int i=0;i<ss.length;i++){
			 if(i!=ss.length-1){
				String user=ss[i]+"}";
				userlist.add(JSONObject.fromObject(user));
			 }else{
				 userlist.add(JSONObject.fromObject(ss[i]));
			 }
		 }
		 List<GuangZhuUser> allUser=new ArrayList<GuangZhuUser>();
		 for(JSONObject j:userlist){
			GuangZhuUser g=new GuangZhuUser();
			g.setId(j.getString("id"));
			g.setGroup_id(j.getString("group_id"));
			g.setNick_name(j.getString("nick_name"));
			g.setRemark_name(j.getString("remark_name"));
			allUser.add(g);
		 }
		 return allUser;
		 
   }
   public static String jsouphtml(String html){
	   Document doc = Jsoup.parse(html);
	   Elements es=doc.getElementsByTag("script");
	   Element e=es.get(es.size()-1);
	    return e.html();
	   
   }
   public static String httpRequest3(String requesturl,String requestMethod,String outputStr){
	   Map<String,String> messageMap=httpRequest("https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN","POST","username=995006199@qq.com&pwd=f01deddcea4d414bc5a4235db76ec6da");
	   StringBuffer buf=new StringBuffer();
	   try {
		   requesturl=requesturl.replace("TOKEN",messageMap.get("token"));
		   URL url=new URL(requesturl);
		   TrustManager[] tm={(TrustManager) new MyX509TrustManager()};
		   SSLContext sslContext=SSLContext.getInstance("SSL","SunJSSE");
		   sslContext.init(null,tm, new java.security.SecureRandom());
		   SSLSocketFactory ssf=sslContext.getSocketFactory();
		   HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
		   conn.setSSLSocketFactory(ssf);
		   conn.setDoOutput(true);
		   conn.setDoInput(true);
		   conn.setUseCaches(false);
		   conn.setConnectTimeout(8000);
		   conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		   String referer="https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=TOKEN";
		   conn.setRequestProperty("Referer",referer.replace("TOKEN",messageMap.get("token")));
		   conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
		   conn.setRequestProperty("Host","mp.weixin.qq.com");//DATA_TICKET SLAVE_USER SLAVE_SID
		   String cookie="remember_acct=995006199%40qq.com; ssuid=7912378624; ts_refer=jingyan.baidu.com/article/6525d4b134051eac7d2e9417.html; ts_uid=6102891860; mpuv=CqOGCFVG8OKHF2TcGXjRAg==; ptui_loginuin=995006199@qq.com; ptcz=24296f219c12d255a6580d695ee381c2b6505340c90f633043950999631ac0c7; pt2gguin=o0995006199; pgv_pvid=5770065052; o_cookie=995006199; pgv_pvi=947729408;data_bizuin=3003524479; data_ticket=DATA_TICKET; slave_user=SLAVE_USER; slave_sid=SLAVE_SID; bizuin=3091820797";
		   conn.setRequestProperty("Cookie",cookie.replace("DATA_TICKET",messageMap.get("data_ticket")).replace("SLAVE_USER",messageMap.get("slave_user")).replace("SLAVE_SID",messageMap.get("slave_sid")));		   
		   conn.setRequestMethod(requestMethod);
		   conn.connect();
		   if(outputStr!=null){
			  OutputStream output=conn.getOutputStream();
			  output.write(outputStr.getBytes("UTF-8"));
			  output.close();
		   }
		   String line=null;
		   InputStream input=conn.getInputStream();
		   BufferedReader bufferStream=new BufferedReader(new InputStreamReader(input,"UTF-8"));
		   while((line=bufferStream.readLine())!=null){
			   buf.append(line);
		   }
		  input.close();
		  bufferStream.close();
		  conn.disconnect();
		  return buf.toString();
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
   }
   
   
   public static Map<String,String> httpRequest(String requesturl,String requestMethod,String outputStr){
	   StringBuffer buf=new StringBuffer();
	   Map<String,String> messageMap=new HashMap<String,String>();
	   try {
		   URL url=new URL(requesturl);
		   TrustManager[] tm={(TrustManager) new MyX509TrustManager()};
		   SSLContext sslContext=SSLContext.getInstance("SSL","SunJSSE");
		   sslContext.init(null,tm, new java.security.SecureRandom());
		   SSLSocketFactory ssf=sslContext.getSocketFactory();
		   HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
		   conn.setSSLSocketFactory(ssf);
		   conn.setDoOutput(true);
		   conn.setDoInput(true);
		   conn.setUseCaches(false);
		   conn.setConnectTimeout(8000);
		   conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		   conn.setRequestProperty("Referer","https://mp.weixin.qq.com/");
		   conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; InfoPath.2; .NET4.0E)");	
		   conn.setRequestProperty("Host","mp.weixin.qq.com");
		   conn.setRequestMethod(requestMethod);
		   conn.connect();
		   if(outputStr!=null){
			  OutputStream output=conn.getOutputStream();
			  output.write(outputStr.getBytes("UTF-8"));
			  output.close();
		   }
		   String line=null;
		   InputStream input=conn.getInputStream();
		   String cookie=conn.getHeaderFields().get("Set-Cookie").toString();
		   Pattern p=Pattern.compile("data_ticket=([\\S]+)");
		   Matcher m=p.matcher(cookie);
		   while(m.find()){
			   messageMap.put("data_ticket",m.group(1).replace(";",""));
		   }
		   p=Pattern.compile("slave_sid=([\\S]+)");
		   m=p.matcher(cookie);
		   while(m.find()){
			   messageMap.put("slave_sid",m.group(1).replace(";",""));
		   }
		   p=Pattern.compile("slave_user=([\\S]+)");
		   m=p.matcher(cookie);
		   while(m.find()){
			   messageMap.put("slave_user",m.group(1).replace(";",""));
		   }
		   BufferedReader bufferStream=new BufferedReader(new InputStreamReader(input,"UTF-8"));
		   while((line=bufferStream.readLine())!=null){
			   buf.append(line);
		   }
		   p=Pattern.compile("token=([0-9]+)");
		   m=p.matcher(buf);
		   while(m.find()){
			   messageMap.put("token",m.group(1));
		   }
		  input.close();
		  bufferStream.close();
		  conn.disconnect();
		  return messageMap;
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
   }
   
   
   
}
