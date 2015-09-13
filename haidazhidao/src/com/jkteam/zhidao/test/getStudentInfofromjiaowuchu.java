package com.jkteam.zhidao.test;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 模拟获取教务处
 * @author ZheTang
 * @date 2015-6-12
 *
 */
public class getStudentInfofromjiaowuchu {
	private static String baseURL = "http://210.38.137.126:8016/default2.aspx";
	private static String cookie = null;
	public static String get(String urlpath,Map<String, String> nameValue) throws Exception {
		URL url = new URL(urlpath);
		URLConnection connection = url.openConnection();
		if(!nameValue.isEmpty()){  //设置请求头内容
			for(Map.Entry<String, String> entry:nameValue.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.connect();
	    Map<String,List<String>> headers = connection.getHeaderFields();
	    String cookietemping = headers.get("Set-Cookie").get(0);
	    cookie = cookietemping.substring(0, cookietemping.indexOf(";"));
	  
	    System.out.println("cookie:"+cookie);
	    
		Scanner in = new Scanner(connection.getInputStream(), "gb2312");
		StringBuilder response = new StringBuilder();
		while (in.hasNext()) {
			response.append(in.nextLine());
			response.append("\n");
		}
		in.close();
		return response.toString();

	}
	
	public static String post(String urlPath,Map<String,String> nameValue,Map<String, String> postValue) throws Exception{
		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		System.out.println("username:"+postValue.get("username"));
		System.out.println("password:"+postValue.get("password"));
		System.out.println("cookie:"+cookie);
		if(!nameValue.isEmpty()){  //设置请求头内容
			for(Map.Entry<String, String> entry:nameValue.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write("__VIEWSTATE=dDwyODE2NTM0OTg7Oz47AX3Zzu1TpcW9kTBcf7gpTWfX4g%3D%3D&" +
				"txtUserName="+postValue.get("username")+
				"&TextBox2="+postValue.get("password")+
				"&txtSecretCode=" +""+
				"&RadioButtonList1=%D1%A7%C9%FA&Button1=&lbLanguage=&hidPdrs=&hidsc=");
		out.flush();
		out.close();
		Scanner in = new Scanner(connection.getInputStream(), "gb2312");
		StringBuilder response = new StringBuilder();
		while (in.hasNext()) {
			response.append(in.nextLine());
			response.append("\n");
		}
		in.close();
		connection.disconnect();
		return response.toString();
	}
	
	public static String login(String username,String password) throws Exception{
		getIndexHtml();
		Map<String, String> nameValue = new HashMap<String, String>();
		Map<String, String> postValue = new HashMap<String, String>();
		nameValue.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
		nameValue.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nameValue.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		nameValue.put("Accept-Encoding", "gzip, deflate");
		nameValue.put("Referer", baseURL);
		nameValue.put("Cookie", cookie);
		nameValue.put("Content-Type", "application/x-www-form-urlencoded");
		nameValue.put("Content-Length","195");
		
		postValue.put("username", username);
		postValue.put("password", password);
		
		return post(baseURL, nameValue, postValue);
	}
	public static String getIndexHtml() throws Exception{
		Map<String, String> nameValue = new HashMap<String, String>();
		nameValue.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
		nameValue.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nameValue.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		nameValue.put("Accept-Encoding", "gzip, deflate");
		nameValue.put("Referer", "http://www.gdou.edu.cn/jw/zf.html");
		
		return get(baseURL, nameValue);
	}
	public static void main(String[] args) throws Exception {
		String response = login("201211621133", "zhetang6360182");
		System.out.println(response);
	}
}
