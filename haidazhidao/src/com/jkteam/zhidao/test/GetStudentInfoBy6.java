package com.jkteam.zhidao.test;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetStudentInfoBy6 {
	private static final String URL ="http://210.38.137.124:8016/default2.aspx"; 
	private static String  baseUrl = null;
	// 用戶登錄
	private static Map<String, String>  subURL =new HashMap<String, String>();//子路径  通过与baseURL合并就可以访问   map中的key（返回首页，专业任选课，重修选课，校公选课，体育选课，学生选课，大学英语四六级报名，毕业生问卷调查，个人信息，密码修改，二专业报名，学生个人课表，班级课表查询，等级考试查询，本专业培养方案，二专业计划查询，全校课程一览表，学生选课情况查询，成绩查询，缓考申请）
    //比如： subURL.get("返回首页") 即可获得：xs_main.aspx?xh=201211621133
	private static String tonggou = null;//通告
	public static String getTonggou() {
	return tonggou;
	}
	
	
	public GetStudentInfoBy6(String username,String userpass){
		init(username,userpass);
	}
	
	/**
	 * 初始化
	 * @param username
	 * @param userpass
	 */
	private  void init(String username, String userpass) {
		try {
			String response = GetStudentInfoBy6.login( username,userpass);
			subURL = GetStudentInfoBy6.parseIndexHtml(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String login(String username,String password) throws Exception{
		
		URL url = new URL(URL);//
		URLConnection conn = url.openConnection();
		conn.connect();
		Map<String, List<String>> headers = conn.getHeaderFields();
		url = conn.getURL();// 获取请求的真正url
		
		baseUrl = (url.toString()).substring(0, (url.toString()).lastIndexOf("/")+1);
		System.out.println(baseUrl);
		
		Map<String, String> nameValue = new HashMap<String, String>();
		Map<String, String> postValue = new HashMap<String, String>();
		nameValue.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
		nameValue.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nameValue.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		nameValue.put("Accept-Encoding", "gzip, deflate");
		nameValue.put("Referer", baseUrl);
		nameValue.put("Content-Type", "application/x-www-form-urlencoded");
		nameValue.put("Content-Length","466");
		
		postValue.put("username", username);
		postValue.put("password", password);
		
		return post(baseUrl+"default6.aspx", nameValue, postValue);
	}
    
	public static String post(String urlPath,Map<String,String> nameValue,Map<String, String> postValue) throws Exception{
		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		if(!nameValue.isEmpty()){  //设置请求头内容
			for(Map.Entry<String, String> entry:nameValue.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write("__VIEWSTATE=dDwtMTQxNDAwNjgwODt0PDtsPGk8MD47PjtsPHQ8O2w8aTwyMT47aTwyMz47aTwyNT47aTwyNz47PjtsPHQ8cDxsPGlubmVyaHRtbDs%2BO2w8Oz4%2BOzs%2BO3Q8cDxsPGlubmVyaHRtbDs%2BO2w8Oz4%2BOzs%2BO3Q8cDxsPGlubmVyaHRtbDs%2BO2w8Oz4%2BOzs%2BO3Q8cDxsPGlubmVyaHRtbDs%2BO2w8Oz4%2BOzs%2BOz4%2BOz4%2BOz6%2BJ8oqLNo4Jt%2BT4C%2BPg%2FsDKqJefQ%3D%3D&tname=&tbtns=&tnameXw=yhdl&tbtnsXw=yhdl%7Cxwxsdl&" +
				"txtYhm="+postValue.get("username")+
				"&txtXm="+postValue.get("password")+
				"&txtMm="+postValue.get("password")+
				"&rblJs=%D1%A7%C9%FA&btnDl=%B5%C7+%C2%BC");
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
	
	
	/**
	 * 解析首页
	 * 获取首页的所有链接
	 * @param html   首页html文本
	 * @return Map<String,String> 链接子集
	 * @throws Exception
	 */
	public static  Map<String,String> parseIndexHtml(String html) throws Exception{
		Map<String,String> urlMap = new HashMap<String, String>();
		Document doc = Jsoup.parse(html);
		Elements links = doc.select("a[href]");
		Element content = doc.getElementById("xhxm");
		Element tonggous = doc.getElementById("xsrs");
		tonggou = tonggous.text();//通告内容
		String studentname = content.text().substring(0,content.text().lastIndexOf("同"));   //郭灶鹏同学
		String replacename =URLEncoder.encode(studentname,"gb2312");//转成gb2312编码
		for (Element link : links) {
		  String linkHref = link.attr("href");
		  String linkText = link.text();
		  if("退出".equals(linkText)){
			  continue;
		  }
		  if("#".equals(linkHref) ||"#a".equals( linkHref)){
			  continue;
		  }else{
			   String truelinkHref = linkHref.replaceAll(studentname, replacename);//真正的url
			   System.out.println(linkText+":"+truelinkHref);
			   urlMap.put(linkText, truelinkHref);
		  }
	     }
		return urlMap;
	}
	public static void main(String[] args) throws Exception {
		/*String response = login("201211621133", "zhetang6360182");
		System.out.println(response);*/
		GetStudentInfoBy6 getStudentInfoBy6 = new GetStudentInfoBy6("201211621133", "zhetang6360182");
	}
}
