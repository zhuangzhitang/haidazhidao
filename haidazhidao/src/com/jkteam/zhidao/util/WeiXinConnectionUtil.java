package com.jkteam.zhidao.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONObject;

import com.jkteam.zhidao.domain.MyX509TrustManager;



/**
 * 调用微信接口通信的工具类
 * @author 郭灶鹏
 *
 */
public class WeiXinConnectionUtil {
  /**
   * 发送https请求
   * @param requesturl URL链接
   * @param requestMethod  请求方式
   * @param outputStr      输出数据，json格式的
   * @return         请求返回的数据。
   */
  public static JSONObject httpRequest(String requesturl,String requestMethod,String outputStr){
	   JSONObject jsonObect=null;
	   StringBuffer buf=new StringBuffer();
	   try {
		   TrustManager[] tm={(TrustManager) new MyX509TrustManager()};
		   SSLContext sslContext=SSLContext.getInstance("SSL","SunJSSE");
		   sslContext.init(null,tm, new java.security.SecureRandom());
		   SSLSocketFactory ssf=sslContext.getSocketFactory();
		   URL url=new URL(requesturl);
		     //注意这里是https请求
		   HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
		   conn.setSSLSocketFactory(ssf);
		   conn.setDoOutput(true);
		   conn.setDoInput(true);
		   conn.setUseCaches(false);
		   conn.setConnectTimeout(8000);
		   conn.setRequestProperty("Content-Type","application/json");
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
		  
		  jsonObect=JSONObject.fromObject(buf.toString());
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
	   return jsonObect;
   }
}
