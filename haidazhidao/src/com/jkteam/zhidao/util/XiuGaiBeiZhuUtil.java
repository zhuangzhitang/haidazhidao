package com.jkteam.zhidao.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;


import net.sf.json.JSONObject;


public class XiuGaiBeiZhuUtil {
	/*
	public static void main(String[] args) {
		System.out.println(xiugaibeizhu("1819681026","o_XBQI"));
	}
	*/
    public static boolean xiugaibeizhu(String id,String openId) {
    	   Map<String,String> messageMap=SendMessageUtil.httpRequest("https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN","POST","username=995006199@qq.com&pwd=f01deddcea4d414bc5a4235db76ec6da");
		   String url="https://mp.weixin.qq.com/cgi-bin/modifycontacts?t=ajax-response&action=setremark";
		   String data="token=TOKEN&lang=zh_CN&f=json&ajax=1&random=0.017686527455225587&remark=REMARK&tofakeuin=TOFAKEUIN";
		   data=data.replace("REMARK",openId).replace("TOFAKEUIN",id).replace("TOKEN",messageMap.get("token"));
		   String huidiao=setRemarkName(url,"POST",data,messageMap);
		   if(JSONObject.fromObject(JSONObject.fromObject(huidiao).getString("base_resp")).getString("err_msg").equals("set remark name ok!")){
			   System.out.println(huidiao);
			   return true; 
		   }
		   return false;
    }
    public static String setRemarkName(String requesturl,String requestMethod,String outputStr,Map<String,String> messageMap){
  	   StringBuffer buf=new StringBuffer();
  	   try {
  		   String cookie="remember_acct=2546923527%40qq.com; ssuid=7912378624;"+
  	                     " ts_refer=jingyan.baidu.com/article/6525d4b134051eac7d2e9417.html;"+
  				         " ts_uid=6102891860; mpuv=CqOGCFVG8OKHF2TcGXjRAg==; ptui_loginuin=2546923527@qq.com;"+
  	                     " ptcz=24296f219c12d255a6580d695ee381c2b6505340c90f633043950999631ac0c7;"+"" +
  	                     " pt2gguin=o0995006199; pgv_pvid=5770065052; o_cookie=2546923527; pgv_pvi=947729408;"+
  	                     " data_bizuin=3003524479; data_ticket=DATA_TICKET; slave_user=SLAVE_USER;"+
  	                     " slave_sid=SLAVE_ID; bizuin=3091820797";
  		   cookie=cookie.replace("DATA_TICKET",messageMap.get("data_ticket")).replace("SLAVE_USER", messageMap.get("slave_user")).replace("SLAVE_ID",messageMap.get("slave_sid"));
  		   URL url=new URL(requesturl);
  		   HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
  		   conn.setDoOutput(true);
  		   conn.setDoInput(true);
  		   conn.setUseCaches(false);
  		   conn.setConnectTimeout(8000);
  		   conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
  		   String referer="https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=0&type=0&token=TOKEN&lang=zh_CN";
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
}
