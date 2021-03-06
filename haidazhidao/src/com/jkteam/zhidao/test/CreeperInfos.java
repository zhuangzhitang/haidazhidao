package com.jkteam.zhidao.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.FreeCourseMain;
import com.jkteam.zhidao.domain.Grade;
import com.jkteam.zhidao.domain.OptionalCourse;
import com.jkteam.zhidao.domain.ReferName;
import com.jkteam.zhidao.util.DateUtil;
/**
 * 针对海大教务系统的爬虫
 * @author ZheTang
 * @date 2015-5-5
 *
 */
public class CreeperInfos {
	private static final String URL ="http://210.38.137.124:8016/default2.aspx";     //正方教育管理系统入口地址一
	private static String baseURL ;  //http://210.38.137.124:8016/(d1dznzjukk3ixbignb1kg545)/  
	private static Map<String, String>  subURL =new HashMap<String, String>();//子路径  通过与baseURL合并就可以访问   map中的key（返回首页，专业任选课，重修选课，校公选课，体育选课，学生选课，大学英语四六级报名，毕业生问卷调查，个人信息，密码修改，二专业报名，学生个人课表，班级课表查询，等级考试查询，本专业培养方案，二专业计划查询，全校课程一览表，学生选课情况查询，成绩查询，缓考申请）
	
	private static String tonggou = "";//通告
	public  String getTonggou() {
		return tonggou;
	}                                         //比如： subURL.get("返回首页") 即可获得：xs_main.aspx?xh=201211621133
	public CreeperInfos(String username,String userpass){
		init(username,userpass);
	}
	
	/**
	 * 初始化
	 * @param username
	 * @param userpass
	 */
	private  void init(String username, String userpass) {
		try {
			String response = CreeperInfos.login( username,userpass);
			subURL = CreeperInfos.parseIndexHtml(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//是否登录成功
	public boolean isLonginSuccess(){
		
		if(subURL==null){
			return false;
		}
		if(subURL.size()>0){
			return true;
		}else{
			return false;
		}
	}
	//调用此方法直接返回学生个人课程表
	public List<Course> getStudentCource() {
		List<Course> courses  = null;
		try {
			String tureurl =baseURL+"/"+subURL.get("学生个人课表");
			String indexurl = baseURL+"/"+subURL.get("返回首页");
			Map<String,String> requtset = new HashMap<String, String>();
			requtset.put("Referer", indexurl);
			String schedule =CreeperInfos.get(tureurl,requtset);
			courses = CreeperInfos.parseSchedule(schedule);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return courses;
	}
	
	//调用此方法直接返回学生个人成绩
	public List<Grade> getStudentGrade(){
		List<Grade> grades = null;
		try {
			String numurl =baseURL+subURL.get("成绩查询");
		//	System.out.println("参数："+getScoreRequest());
			String content = CreeperInfos.getScore(numurl,getScoreRequest());
			grades = CreeperInfos.parseScore(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grades;
	}
	//调用此方法直接返回学校所有体育课
	public  Set<String> getSchoolSportName(){
		try {
			String  indexurl = baseURL+ subURL.get("返回首页");  
			String  tiyuurl = baseURL+ subURL.get("体育选课");
			Map<String,String> requtst = new HashMap<String, String>();
			requtst.put("Referer", indexurl);
			requtst.put("Cookie", "tabId=1");
			String tihtml =CreeperInfos.get(tiyuurl,requtst);
			List<String> lists = CreeperInfos.parseTY(tihtml);
			return CreeperInfos.parseTyForName(tiyuurl,lists);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//获取学校公选课
	public Set<OptionalCourse> getSchoolOpenSource(){
		Set<OptionalCourse> optionalCourses = null;
		try {
			String openSelecturl = baseURL+"/"+subURL.get("校公选课");
			Map<String,String> nameValue = new HashMap<String, String>();
			String reponse = CreeperInfos.getopenSelectSource(openSelecturl);
			FileWriter f=new FileWriter(new File("D://a.txt"));
			PrintWriter p=new PrintWriter(f);
			p.write(reponse);
			optionalCourses = CreeperInfos.parseOpenSelectSource(reponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return optionalCourses;
	}
	
	//获取成绩查询要提交的past内容
	public static String getScoreRequest() throws Exception{
		 String referer =subURL.get("返回首页");
		 Map<String, String> nameValue = new HashMap<String, String>();
		 nameValue.put("Referer", baseURL+referer);
		 String urlpath = subURL.get("成绩查询");
		 String response = get(baseURL+urlpath, nameValue);
		String value =parseScorereq(response);
		return value;
	}
	public static String parseScorereq(String reqString){
		Document doc = Jsoup.parse(reqString);
		Elements inputs = doc.select("input");
		String value ="__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=";
		for(Element input:inputs){
			if(input.attr("name").equals("__VIEWSTATE")){
				String temp=input.attr("value");
				temp = temp.replaceAll("\\+", "%2B");
				value+=temp;
				break;
			}
		}
		value+="&hidLanguage=&ddlXN=&ddlXQ=&ddl_kcxz=&btn_zcj=%C0%FA%C4%EA%B3%C9%BC%A8";
		return value;
	}
	// get提交
	public static String get(String urlpath,Map<String, String> nameValue) throws Exception {
		URL url = new URL(urlpath);
		URLConnection connection = url.openConnection();
		if(!nameValue.isEmpty()){  //设置请求头内容
			for(Map.Entry<String, String> entry:nameValue.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.connect();
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
	 * post提交获取html内容
	 * @param urlpath   URL路径
	 * @param nameValue 请求头的内容
	 * @param submitContent   提交的内容
	 * @return            请求的网页内容
	 * @throws Exception
	 */
	public static String gethtmlBypost(String urlpath,Map<String, String> nameValue,String submitContent) throws Exception{
		URL url = new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		if(!nameValue.isEmpty()){           //设置请求头
			for(Map.Entry<String, String> entry:nameValue.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write(submitContent);     //提交内容
		out.flush();
		out.close();
		Scanner in = new Scanner(connection.getInputStream(), "gb2312");   //设置编码
		StringBuilder response = new StringBuilder();
		while (in.hasNext()) {
			response.append(in.nextLine());
			response.append("\n");
		}
		in.close();
		connection.disconnect();
		return response.toString();
		
	}
	
	// 用戶登錄
public static String login(String username,String password) throws Exception{
		
		URL url = new URL(URL);//
		URLConnection conn = url.openConnection();
		conn.connect();
		Map<String, List<String>> headers = conn.getHeaderFields();
		url = conn.getURL();// 获取请求的真正url
		
		baseURL = (url.toString()).substring(0, (url.toString()).lastIndexOf("/")+1);
		System.out.println(baseURL);
		
		Map<String, String> nameValue = new HashMap<String, String>();
		Map<String, String> postValue = new HashMap<String, String>();
		nameValue.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
		nameValue.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nameValue.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		nameValue.put("Accept-Encoding", "gzip, deflate");
		nameValue.put("Referer", baseURL);
		nameValue.put("Content-Type", "application/x-www-form-urlencoded");
		nameValue.put("Content-Length","466");
		
		postValue.put("username", username);
		postValue.put("password", password);
		
		return post(baseURL+"default6.aspx", nameValue, postValue);
	}
	

	
	//成绩查询
	public static String getScore(String urlpath,String postcontent) throws Exception{
		System.out.println("---------------"+urlpath);
		URL url = new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Referer",
				urlpath);
		connection.setRequestProperty("Content-Length", "4097");
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write(postcontent);
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

	// 体育查询
	public static String getTy(String urlpath,String kj) throws Exception{
		URL url = new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("Referer",urlpath);
		connection.setRequestProperty("Cookie", "tabId=1");
		connection.setRequestProperty("Content-Length", "3669");
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write("__EVENTTARGET=kj&__EVENTARGUMENT=&__VIEWSTATE=dDwtNTM3Mzg0NTQ5O3Q8cDxsPFhNO1hZO1pZTUM7Tko7WFpCO1pZRE07WEtYTjtYS1hROz47bDzluoTmmbrloII75L%2Bh5oGv5a2m6ZmiO%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacrzsyMDEyO%2BiuoeenkTExMjE7MTYyMTsyMDE0LTIwMTU7Mjs%2BPjtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8ND47aTw2PjtpPDg%2BO2k8MTI%2BO2k8MTY%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPOWnk%2BWQje%2B8muW6hOaZuuWggiZuYnNwXDsmbmJzcFw7Jm5ic3BcOyZuYnNwXDvlrabpmaLvvJrkv6Hmga%2FlrabpmaImbmJzcFw7Jm5ic3BcOyZuYnNwXDsmbmJzcFw75LiT5Lia77ya6K6h566X5py656eR5a2m5LiO5oqA5pyvOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror77ml7bpl7TvvJo7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8c2o7c2o7Pj47Pjt0PGk8MjU%2BO0A8LS3or7fpgInmi6ktLTvjgJDlkajlm5vnrKw1LDboioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysMyw06IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS4ieesrDcsOOiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuIDnrKwzLDToioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LiJ56ysNSw26IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOWbm%2BesrDMsNOiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuInnrKwzLDToioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LqU56ysNSw26IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS4gOesrDUsNuiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuoznrKw3LDjoioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5Zub56ysNyw46IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOWbm%2BesrDUsNuiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuInnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqM56ysMyw06IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS4gOesrDcsOOiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuIDnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysNyw46IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS6jOesrDUsNuiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajlm5vnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysNSw26IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS6jOesrDUsNuiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuIDnrKw1LDboioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LqU56ysMyw06IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS6jOesrDMsNOiKgnvnrKwxLTE15ZGofeOAkTs%2BO0A8LS3or7fpgInmi6ktLTvjgJDlkajlm5vnrKw1LDboioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysMyw06IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS4ieesrDcsOOiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuIDnrKwzLDToioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LiJ56ysNSw26IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOWbm%2BesrDMsNOiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuInnrKwzLDToioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LqU56ysNSw26IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS4gOesrDUsNuiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuoznrKw3LDjoioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5Zub56ysNyw46IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOWbm%2BesrDUsNuiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuInnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqM56ysMyw06IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS4gOesrDcsOOiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajkuIDnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysNyw46IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS6jOesrDUsNuiKgnvnrKwxLTE15ZGofeOAkTvjgJDlkajlm5vnrKwzLDToioJ756ysMS0xNeWRqH3jgJE744CQ5ZGo5LqU56ysNSw26IqCe%2BesrDEtMTXlkah944CRO%2BOAkOWRqOS6jOesrDUsNuiKgnvnrKw0LTE35ZGofeOAkTvjgJDlkajkuIDnrKw1LDboioJ756ysNC0xN%2BWRqH3jgJE744CQ5ZGo5LqU56ysMyw06IqCe%2BesrDQtMTflkah944CRO%2BOAkOWRqOS6jOesrDMsNOiKgnvnrKwxLTE15ZGofeOAkTs%2BPjtsPGk8MD47Pj47Oz47dDxwPHA8bDxUZXh0Oz47bDzvvIjlhbEw5p2h6K6w5b2V77yB77yJOz4%2BOz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MD47aTwwPjtsPD47Pj47PjtAMDw7Ozs7Ozs7Ozs7QDA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Ozs7Pjs7Ozs7Pjs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MD47aTwwPjtsPD47Pj47PjtAMDw7Ozs7Ozs7Ozs7QDA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Ozs7Pjs%2BOzs7Ozs7Ozs7Pjs7Pjt0PHA8O3A8bDxvbmNsaWNrOz47bDx3aW5kb3cuY2xvc2UoKVw7Oz4%2BPjs7Pjs%2BPjs%2BPjs%2B2E2759Q4QWUEkdaDciJJ3zWd6Bs%3D&kj="+kj);
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
	//校公选课查询
	public static String getopenSelectSource(String urlpath) throws Exception{
		URL url = new URL(urlpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("Referer",urlpath);
		connection.setRequestProperty("Cookie", "tabId=1");
		connection.setRequestProperty("Content-Length", "32735");
		connection.setRequestProperty("Cache-Control", "max-age=0");
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write("__EVENTTARGET=ddl_ywyl&__EVENTARGUMENT=&__VIEWSTATE=dDwtNzM2NDcyNjkwO3Q8cDxsPGRxc3pqO3p5ZG07WEtYTjtYS1hRO3NvcnRPcmRlcjs%2BO2w8MjAxMjsxNjIxOzIwMTQtMjAxNTsyOyBhc2MgOz4%2BO2w8aTwxPjs%2BO2w8dDw7bDxpPDE%2BO2k8Mz47aTw1PjtpPDc%2BO2k8OT47aTwxND47aTwyMj47aTwyMz47aTwyNT47aTwyNz47aTwyOD47aTwzMj47aTwzND47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w85aeT5ZCN77ya5bqE5pm65aCCJm5ic3BcOyZuYnNwXDsmbmJzcFw7Jm5ic3BcO%2BWtpumZou%2B8muS%2FoeaBr%2BWtpumZoiZuYnNwXDsmbmJzcFw7Jm5ic3BcOyZuYnNwXDvkuJPkuJrvvJrorqHnrpfmnLrnp5HlrabkuI7mioDmnK87Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8a2N4ejtrY3h6Oz4%2BOz47dDxpPDI%2BO0A85Lu76YCJO1xlOz47QDzku7vpgIk7XGU7Pj47bDxpPDE%2BOz4%2BOzs%2BO3Q8dDw7cDxsPGk8MD47aTwxPjtpPDI%2BOz47bDxwPOaciTvmnIk%2BO3A85pegO%2BaXoD47cDxcZTtcZT47Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxrY2dzO2tjZ3M7Pj47Pjt0PGk8NT47QDzmtbfmtIvntKDotKjmlZnogrLnsbs756eR56CU5LiO5Yib5paw57G7O%2BS6uuaWh%2BekvuS8muenkeWtpuexuzvoh6rnhLbnp5Hlrabnsbs7XGU7PjtAPOa1t%2Ba0i%2Be0oOi0qOaVmeiCsuexuzvnp5HnoJTkuI7liJvmlrDnsbs75Lq65paH56S%2B5Lya56eR5a2m57G7O%2BiHqueEtuenkeWtpuexuztcZTs%2BPjtsPGk8ND47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhxbWM7eHFkbTs%2BPjs%2BO3Q8aTwxPjtAPOa5luWFieagoeWMujs%2BO0A8MTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHNrc2o7c2tzajs%2BPjs%2BO3Q8aTwzMz47QDzlkajkuoznrKw1LDboioJ756ysMS0xMuWRqH075ZGo5LqM56ysNyw46IqCe%2BesrDEtMTLlkah9O%2BWRqOS6jOesrDksMTDoioJ756ysMS0xMuWRqH075ZGo5LqM56ysOSwxMOiKgnvnrKwxLTjlkah9O%2BWRqOWFreesrDEsMuiKgnvnrKwxLTEy5ZGofTvlkajlha3nrKwxLDLoioJ756ysMS0xNuWRqH075ZGo5YWt56ysMSwy6IqCe%2BesrDEtOOWRqH075ZGo5YWt56ysMSwy6IqCe%2BesrDUtMTLlkah9O%2BWRqOWFreesrDMsNOiKgnvnrKwxLTEy5ZGofTvlkajlha3nrKwzLDToioJ756ysMS045ZGofTvlkajlha3nrKwzLDToioJ756ysNS0xMuWRqH075ZGo5YWt56ysNSw26IqCe%2BesrDEtMTLlkah9O%2BWRqOWFreesrDUsNuiKgnvnrKwxLTjlkah9O%2BWRqOWFreesrDUsNuiKgnvnrKw1LTEy5ZGofTvlkajlha3nrKw3LDjoioJ756ysMS0xMuWRqH075ZGo5YWt56ysNyw46IqCe%2BesrDEtOOWRqH075ZGo5LiJ56ysOSwxMOiKgnvnrKwxLTEy5ZGofTvlkajkuInnrKw5LDEw6IqCe%2BesrDEtOOWRqH075ZGo5Zub56ysNSw26IqCe%2BesrDEtMTLlkah9O%2BWRqOWbm%2BesrDUsNuiKgnvnrKwxLTjlkah9O%2BWRqOWbm%2BesrDcsOOiKgnvnrKwxLTEy5ZGofTvlkajlm5vnrKw3LDjoioJ756ysMS045ZGofTvlkajlm5vnrKw5LDEw6IqCe%2BesrDEtMTLlkah9O%2BWRqOWbm%2BesrDksMTDoioJ756ysMS045ZGofTvlkajkupTnrKw1LDboioJ756ysMS0xMuWRqH075ZGo5LqU56ysNyw46IqCe%2BesrDEtMTLlkah9O%2BWRqOS6lOesrDksMTDoioJ756ysMS0xMuWRqH075ZGo5LqU56ysOSwxMOiKgnvnrKwxLTjlkah9O%2BWRqOS4gOesrDUsNuiKgnvnrKwxLTEy5ZGofTvlkajkuIDnrKw3LDjoioJ756ysMS0xMuWRqH075ZGo5LiA56ysOSwxMOiKgnvnrKwxLTEy5ZGofTvlkajkuIDnrKw5LDEw6IqCe%2BesrDEtOOWRqH07XGU7PjtAPOWRqOS6jOesrDUsNuiKgnvnrKwxLTEy5ZGofTvlkajkuoznrKw3LDjoioJ756ysMS0xMuWRqH075ZGo5LqM56ysOSwxMOiKgnvnrKwxLTEy5ZGofTvlkajkuoznrKw5LDEw6IqCe%2BesrDEtOOWRqH075ZGo5YWt56ysMSwy6IqCe%2BesrDEtMTLlkah9O%2BWRqOWFreesrDEsMuiKgnvnrKwxLTE25ZGofTvlkajlha3nrKwxLDLoioJ756ysMS045ZGofTvlkajlha3nrKwxLDLoioJ756ysNS0xMuWRqH075ZGo5YWt56ysMyw06IqCe%2BesrDEtMTLlkah9O%2BWRqOWFreesrDMsNOiKgnvnrKwxLTjlkah9O%2BWRqOWFreesrDMsNOiKgnvnrKw1LTEy5ZGofTvlkajlha3nrKw1LDboioJ756ysMS0xMuWRqH075ZGo5YWt56ysNSw26IqCe%2BesrDEtOOWRqH075ZGo5YWt56ysNSw26IqCe%2BesrDUtMTLlkah9O%2BWRqOWFreesrDcsOOiKgnvnrKwxLTEy5ZGofTvlkajlha3nrKw3LDjoioJ756ysMS045ZGofTvlkajkuInnrKw5LDEw6IqCe%2BesrDEtMTLlkah9O%2BWRqOS4ieesrDksMTDoioJ756ysMS045ZGofTvlkajlm5vnrKw1LDboioJ756ysMS0xMuWRqH075ZGo5Zub56ysNSw26IqCe%2BesrDEtOOWRqH075ZGo5Zub56ysNyw46IqCe%2BesrDEtMTLlkah9O%2BWRqOWbm%2BesrDcsOOiKgnvnrKwxLTjlkah9O%2BWRqOWbm%2BesrDksMTDoioJ756ysMS0xMuWRqH075ZGo5Zub56ysOSwxMOiKgnvnrKwxLTjlkah9O%2BWRqOS6lOesrDUsNuiKgnvnrKwxLTEy5ZGofTvlkajkupTnrKw3LDjoioJ756ysMS0xMuWRqH075ZGo5LqU56ysOSwxMOiKgnvnrKwxLTEy5ZGofTvlkajkupTnrKw5LDEw6IqCe%2BesrDEtOOWRqH075ZGo5LiA56ysNSw26IqCe%2BesrDEtMTLlkah9O%2BWRqOS4gOesrDcsOOiKgnvnrKwxLTEy5ZGofTvlkajkuIDnrKw5LDEw6IqCe%2BesrDEtMTLlkah9O%2BWRqOS4gOesrDksMTDoioJ756ysMS045ZGofTtcZTs%2BPjtsPGk8MzI%2BOz4%2BOzs%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDI%2BO2k8Mj47bDw%2BOz4%2BOz47QDA8O0AwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs7Oz47Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Pjs7Ozs7Ozs7Oz47bDxpPDA%2BOz47bDx0PDtsPGk8MT47aTwyPjs%2BO2w8dDw7bDxpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BO2k8OD47aTw5PjtpPDEwPjtpPDExPjtpPDEyPjtpPDEzPjtpPDE0PjtpPDE1PjtpPDE2PjtpPDE3PjtpPDE4PjtpPDE5PjtpPDIwPjtpPDIxPjtpPDIyPjtpPDIzPjtpPDI1PjtpPDI2PjtpPDI3PjtpPDI4Pjs%2BO2w8dDw7bDxpPDM%2BOz47bDx0PHA8bDxvbmNsaWNrOz47bDxzaG93KHRoaXMsJ%2Bingui1j%2BWbreiJuuWtpnzkuK3lm73lhpzkuJrlh7rniYjnpL586ZmI5Y%2BR5qOj44CB6YOt57u05piOfDInKTs%2BPjs7Pjs%2BPjt0PHA8cDxsPFRleHQ7PjtsPFw8YSBocmVmPScjJyBvbmNsaWNrPSJ3aW5kb3cub3Blbigna2N4eC5hc3B4P3hoPTIwMTIxMTYyMTEzMyZrY2RtPTEzMjkyMDAyeDAmeGtraD0oMjAxNC0yMDE1LTIpLTEzMjkyMDAyeDAtMTMyMDA5LTEnLCdrY3h4JywndG9vbGJhcj0wLGxvY2F0aW9uPTAsZGlyZWN0b3JpZXM9MCxzdGF0dXM9MCxtZW51YmFyPTAsc2Nyb2xsYmFycz0xLHJlc2l6YWJsZT0wLHdpZHRoPTU0MCxoZWlnaHQ9NDUwLGxlZnQ9MTIwLHRvcD02MCcpIlw%2B6KeC6LWP5Zut6Im6XDwvYVw%2BOz4%2BOz47Oz47dDw7bDxpPDE%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPOacquS4iuS8oDs%2BPjs%2BOzs%2BOz4%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTMyOTIwMDJ4MDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8XDxhIGhyZWY9JyMnIG9uY2xpY2s9IndpbmRvdy5vcGVuKCdqc3h4LmFzcHg%2FeGg9MjAxMjExNjIxMTMzJmpzemdoPTEzMjAwOSZ4a2toPSgyMDE0LTIwMTUtMiktMTMyOTIwMDJ4MC0xMzIwMDktMScsJ2pzeHgnLCd0b29sYmFyPTAsbG9jYXRpb249MCxkaXJlY3Rvcmllcz0wLHN0YXR1cz0wLG1lbnViYXI9MCxzY3JvbGxiYXJzPTEscmVzaXphYmxlPTAsd2lkdGg9NTQwLGhlaWdodD00NTAsbGVmdD0xMjAsdG9wPTYwJykiXD7liJjku5jkuJzmoIdcPC9hXD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VG9vbFRpcDs%2BO2w85ZGo5YWt56ysMSwy6IqCe%2BesrDEuLi475ZGo5YWt56ysMSwy6IqCe%2BesrDEtMTLlkah9Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuLvmpbw0MTMo5aSaKTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MS41Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwyLjAtMC4wOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwwMS0xMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTA1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMDQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCgyMDE0LTIwMTUtMiktMTMyOTIwMDJ4MC0xMzIwMDktMTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTMyOTIwMDJ4MDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTMyMDA5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzoh6rnhLbnp5Hlrabnsbs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS7u%2BmAiTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85rmW5YWJ5qCh5Yy6Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlhpzlrabpmaI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47aTw4PjtpPDk%2BO2k8MTA%2BO2k8MTE%2BO2k8MTI%2BO2k8MTM%2BO2k8MTQ%2BO2k8MTU%2BO2k8MTY%2BO2k8MTc%2BO2k8MTg%2BO2k8MTk%2BO2k8MjA%2BO2k8MjE%2BO2k8MjI%2BO2k8MjM%2BO2k8MjU%2BO2k8MjY%2BO2k8Mjc%2BO2k8Mjg%2BOz47bDx0PDtsPGk8Mz47PjtsPHQ8cDxsPG9uY2xpY2s7VmlzaWJsZTs%2BO2w8c2hvdyh0aGlzLCd8fHwnKTtvPGY%2BOz4%2BOzs%2BOz4%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8XDxhIGhyZWY9JyMnIG9uY2xpY2s9IndpbmRvdy5vcGVuKCdrY3h4LmFzcHg%2FeGg9MjAxMjExNjIxMTMzJmtjZG09NTkxOTIwMDF4MCZ4a2toPSgyMDE0LTIwMTUtMiktNTkxOTIwMDF4MC0xNjQwMDktMScsJ2tjeHgnLCd0b29sYmFyPTAsbG9jYXRpb249MCxkaXJlY3Rvcmllcz0wLHN0YXR1cz0wLG1lbnViYXI9MCxzY3JvbGxiYXJzPTEscmVzaXphYmxlPTAsd2lkdGg9NTQwLGhlaWdodD00NTAsbGVmdD0xMjAsdG9wPTYwJykiXD7lpJrlqpLkvZPliLbkvZzmioDmnK9cPC9hXD47Pj47Pjs7Pjt0PDtsPGk8MT47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w85pyq5LiK5LygOz4%2BOz47Oz47Pj47dDxwPHA8bDxUZXh0Oz47bDw1OTE5MjAwMXgwOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDxcPGEgaHJlZj0nIycgb25jbGljaz0id2luZG93Lm9wZW4oJ2pzeHguYXNweD94aD0yMDEyMTE2MjExMzMmanN6Z2g9MTY0MDA5Jnhra2g9KDIwMTQtMjAxNS0yKS01OTE5MjAwMXgwLTE2NDAwOS0xJywnanN4eCcsJ3Rvb2xiYXI9MCxsb2NhdGlvbj0wLGRpcmVjdG9yaWVzPTAsc3RhdHVzPTAsbWVudWJhcj0wLHNjcm9sbGJhcnM9MSxyZXNpemFibGU9MCx3aWR0aD01NDAsaGVpZ2h0PTQ1MCxsZWZ0PTEyMCx0b3A9NjAnKSJcPuWQtOaVj1w8L2FcPjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDtUb29sVGlwOz47bDzlkajkuoznrKw1LDboioJ756ysMS4uLjvlkajkuoznrKw1LDboioJ756ysMS0xMuWRqH07Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOmSn%2Ba1t%2BalvDA2MDA4Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxLjU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDIuMC0wLjA7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDAxLTEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw3MDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Njc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDM7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCgyMDE0LTIwMTUtMiktNTkxOTIwMDF4MC0xNjQwMDktMTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8NTkxOTIwMDF4MDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTY0MDA5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzoh6rnhLbnp5Hlrabnsbs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS7u%2BmAiTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85rmW5YWJ5qCh5Yy6Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlrp7pqozmlZnlrabpg6g7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjt0PHA8cDxsPFF1ZXJ5O2R0UmVjb3Jkczs%2BO2w8c2VsZWN0ICogZnJvbSAoc2VsZWN0IGEua2NkbSxhLmtjbWMsYS5qc3pnaCxhLmpzeG0sYS5za3NqLGEuc2tkZCxhLnhmLGEuenhzLGEucXNqc3osYS54a2toLGEueHFicyxhLmtjZ3MsYS5rY3h6LG52bChhLnJzLDApIHJzLChzZWxlY3QgY291bnQoeGgpIGZyb20geHN4a2JuIGYgd2hlcmUgYS54a2toPWYueGtraCkgICB5eHJzLCcnIHlsLGEuYnosYS5ta3poICxhLmtreHksYS5rc3NqLCcnIHNmYngsYS5rc3hzLGEuc3FzbSAsIGNhc2Ugd2hlbiBiLmZqZHogaXMgbnVsbCB0aGVuICcnIGVsc2UgIGIuZmpkeiBlbmQgZmp4eiAsIGpjbWN8fCd8J3x8Y2JzfHwnfCd8fHp6fHwnfCd8fGJiIGpjbnIgZnJvbSAgeHhranhyd2IgIGEgICxqc2p4cmxiX2ZqZHogYiAgd2hlcmUgICBhLnhra2ggbGlrZSAnKDIwMTQtMjAxNS0yKS0lJyBhbmQgYS54a2toPWIueGtraCgrKSAgYW5kIGEueGtraCBsaWtlICcoMjAxNC0yMDE1LTIpLSUnIGFuZCBhLnhrenQ9JzEnICAgYW5kIGV4aXN0cyAoc2VsZWN0ICd4JyBmcm9tICggc2VsZWN0ICBhLnhrbWMsYS5iaCAgZnJvbSAgeGtkbWIgYSAsenlkbWIgYiB3aGVyZSBiLnp5ZG09JzE2MjEnIGFuZCBiLmt4bGIgbGlrZSAnJSd8fGEuYmh8fCclJyl4IHdoZXJlIGEua2Nncz14LnhrbWMpIGFuZCBhLnhxYnM9J%2Ba5luWFieagoeWMuicgIGFuZCAobXhkeCBpcyBudWxsIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMjExNjIxMTMzJ3x8JywlJyAgICAgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn6K6h566X5py656eR5a2m5LiO5oqA5pyv5peg5pa55ZCRJ3x8JywlJyAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6p%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BaXoOaWueWQkSd8fCcsJScgICAgb3IgbXhkeCBsaWtlICclLCd8fCfmnKznp5EnfHwnLCUnICAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCfmnKznp5EyMDEy57qnJ3x8JywlJyAgICAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BacrOenkeeUt%2BeUnyd8fCcsJScgICAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCfmnKznp5Hkv6Hmga%2FlrabpmaInfHwnLCUnICAgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eRMjAxMue6p%2BS%2FoeaBr%2BWtpumZoid8fCcsJScgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eRMjAxMue6p%2BeUt%2BeUnyd8fCcsJScgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eR5L%2Bh5oGv5a2m6Zmi55S355SfJ3x8JywlJyAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCfmnKznp5EyMDEy57qn5L%2Bh5oGv5a2m6Zmi55S355SfJ3x8JywlJyAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6pyd8fCcsJScgICAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCfkv6Hmga%2FlrabpmaInfHwnLCUnICAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnrpfmnLrnp5HlrabkuI7mioDmnK8nfHwnLCUnICAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnp5ExMTIxJ3x8JywlJyAgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn55S355SfJ3x8JywlJyAgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6p%2BS%2FoeaBr%2BWtpumZoid8fCcsJScgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6p%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacryd8fCcsJScgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6p%2BeUt%2BeUnyd8fCcsJScgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5L%2Bh5oGv5a2m6Zmi55S355SfJ3x8JywlJyAgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2FnlLfnlJ8nfHwnLCUnICAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BiuoeenkTExMjHnlLfnlJ8nfHwnLCUnICAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8JzIwMTLnuqfkv6Hmga%2FlrabpmaLnlLfnlJ8nfHwnLCUnICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCcyMDEy57qn6K6h566X5py656eR5a2m5LiO5oqA5pyv55S355SfJ3x8JywlJyAgICBvciBteGR4IGxpa2UgJyUsJ3x8JzIwMTLnuqfoi7EnfHwnLCUnICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5L%2Bh5oGv5a2m6Zmi6IuxJ3x8JywlJyAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BiLsSd8fCcsJScgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnp5ExMTIx6IuxJ3x8JywlJyAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8JzIwMTLnuqfkv6Hmga%2FlrabpmaLoi7EnfHwnLCUnICAgICBvciBteGR4IGxpa2UgJyUsJ3x8JzIwMTLnuqforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2Foi7EnfHwnLCUnICAgb3IgbXhkeCBsaWtlICclLCd8fCfoi7EnfHwnLCUnICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BacrOenkeiLsSd8fCcsJScgICAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfoi7EnfHwnLCUnICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eR5L%2Bh5oGv5a2m6Zmi6IuxJ3x8JywlJyAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfkv6Hmga%2FlrabpmaLoi7EnfHwnLCUnICAgICBvciBteGR4IGxpa2UgJyUsJ3x8JzIwMTLnuqfnlLfnlJ%2Foi7EnfHwnLCUnICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5L%2Bh5oGv5a2m6Zmi55S355Sf6IuxJ3x8JywlJyAgICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2FnlLfnlJ%2Foi7EnfHwnLCUnICAgb3IgbXhkeCBsaWtlICclLCd8fCforqHnp5ExMTIx55S355Sf6IuxJ3x8JywlJyAgICAgb3IgbXhkeCBsaWtlICclLCd8fCcyMDEy57qn5L%2Bh5oGv5a2m6Zmi55S355Sf6IuxJ3x8JywlJyAgICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwnMjAxMue6p%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BeUt%2BeUn%2BiLsSd8fCcsJScgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCfmnKznp5HnlLfnlJ%2Foi7EnfHwnLCUnICAgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eRMjAxMue6p%2BeUt%2BeUn%2BiLsSd8fCcsJScgICAgIG9yIG14ZHggbGlrZSAnJSwnfHwn5pys56eR5L%2Bh5oGv5a2m6Zmi55S355Sf6IuxJ3x8JywlJyAgICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfkv6Hmga%2FlrabpmaLnlLfnlJ%2Foi7EnfHwnLCUnICAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BaXoOaWueWQkeiLsSd8fCcsJScgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCcyMDEy57qn6K6h566X5py656eR5a2m5LiO5oqA5pyv5peg5pa55ZCR6IuxJ3x8JywlJyAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BiuoeWIkid8fCcsJScgICAgICAgb3IgbXhkeCBsaWtlICclLCd8fCcyMDEy57qn6K6h5YiSJ3x8JywlJyAgICBvciBteGR4IGxpa2UgJyUsJ3x8J%2BS%2FoeaBr%2BWtpumZoid8fCcsJScgICAgICApICAgICBhbmQgKHh6ZHggaXMgbnVsbCBvciB4emR4IG5vdCBsaWtlICclLCd8fCcyMDEyMTE2MjExMzMnfHwnLCUnICAgICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn6K6h566X5py656eR5a2m5LiO5oqA5pyv5peg5pa55ZCRJ3x8JywlJyAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8JzIwMTLnuqforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2Fml6DmlrnlkJEnfHwnLCUnIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCfmnKznp5EnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn5pys56eRMjAxMue6pyd8fCcsJScgICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn5pys56eR55S355SfJ3x8JywlJyAgICAgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCfmnKznp5Hkv6Hmga%2FlrabpmaInfHwnLCUnICAgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfkv6Hmga%2FlrabpmaInfHwnLCUnICAgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCfmnKznp5EyMDEy57qn55S355SfJ3x8JywlJyAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn5pys56eR5L%2Bh5oGv5a2m6Zmi55S355SfJ3x8JywlJyAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn5pys56eRMjAxMue6p%2BS%2FoeaBr%2BWtpumZoueUt%2BeUnyd8fCcsJScgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCcyMDEy57qnJ3x8JywlJyAgICAgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCfkv6Hmga%2FlrabpmaInfHwnLCUnICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn6K6h566X5py656eR5a2m5LiO5oqA5pyvJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8J%2BiuoeenkTExMjEnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn55S355SfJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8JzIwMTLnuqfkv6Hmga%2FlrabpmaInfHwnLCUnICAgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCcyMDEy57qn6K6h566X5py656eR5a2m5LiO5oqA5pyvJ3x8JywlJyAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwnMjAxMue6p%2BeUt%2BeUnyd8fCcsJScgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8J%2BS%2FoeaBr%2BWtpumZoueUt%2BeUnyd8fCcsJScgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8J%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BeUt%2BeUnyd8fCcsJScgICAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8J%2BiuoeenkTExMjHnlLfnlJ8nfHwnLCUnICAgIGFuZCB4emR4IG5vdCBsaWtlICclLCd8fCcyMDEy57qn5L%2Bh5oGv5a2m6Zmi55S355SfJ3x8JywlJyAgYW5kIHh6ZHggbm90IGxpa2UgJyUsJ3x8JzIwMTLnuqforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2FnlLfnlJ8nfHwnLCUnICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8JzIwMTLnuqfoi7EnfHwnLCUnICAgICAgIGFuZCB4emR4IG5vdCAgbGlrZSAnJSwnfHwn5L%2Bh5oGv5a2m6Zmi6IuxJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8J%2Biuoeeul%2BacuuenkeWtpuS4juaKgOacr%2BiLsSd8fCcsJScgICAgICAgYW5kIHh6ZHggbm90ICBsaWtlICclLCd8fCforqHnp5ExMTIx6IuxJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8JzIwMTLnuqfkv6Hmga%2FlrabpmaLoi7EnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8JzIwMTLnuqforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2Foi7EnfHwnLCUnICAgIGFuZCB4emR4IG5vdCAgbGlrZSAnJSwnfHwn6IuxJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90ICBsaWtlICclLCd8fCfmnKznp5Hoi7EnfHwnLCUnICAgICAgICAgYW5kIHh6ZHggbm90ICBsaWtlICclLCd8fCfmnKznp5EyMDEy57qn6IuxJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8J%2BacrOenkeS%2FoeaBr%2BWtpumZouiLsSd8fCcsJScgICAgICAgYW5kIHh6ZHggbm90ICBsaWtlICclLCd8fCfmnKznp5EyMDEy57qn5L%2Bh5oGv5a2m6Zmi6IuxJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90ICAgbGlrZSAnJSwnfHwnMjAxMue6p%2BeUt%2BeUn%2BiLsSd8fCcsJScgICAgICAgYW5kIHh6ZHggbm90ICAgbGlrZSAnJSwnfHwn5L%2Bh5oGv5a2m6Zmi55S355Sf6IuxJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90ICAgbGlrZSAnJSwnfHwn6K6h566X5py656eR5a2m5LiO5oqA5pyv55S355Sf6IuxJ3x8JywlJyAgICBhbmQgeHpkeCBub3QgICBsaWtlICclLCd8fCforqHnp5ExMTIx55S355Sf6IuxJ3x8JywlJyAgICAgYW5kIHh6ZHggbm90ICAgbGlrZSAnJSwnfHwnMjAxMue6p%2BS%2FoeaBr%2BWtpumZoueUt%2BeUn%2BiLsSd8fCcsJScgICAgICAgICBhbmQgeHpkeCBub3QgICBsaWtlICclLCd8fCcyMDEy57qn6K6h566X5py656eR5a2m5LiO5oqA5pyv55S355Sf6IuxJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgICBsaWtlICclLCd8fCfmnKznp5HnlLfnlJ%2Foi7EnfHwnLCUnICAgICAgIGFuZCB4emR4IG5vdCAgIGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfnlLfnlJ%2Foi7EnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgICBsaWtlICclLCd8fCfmnKznp5Hkv6Hmga%2FlrabpmaLnlLfnlJ%2Foi7EnfHwnLCUnICAgICAgIGFuZCB4emR4IG5vdCAgIGxpa2UgJyUsJ3x8J%2BacrOenkTIwMTLnuqfkv6Hmga%2FlrabpmaLnlLfnlJ%2Foi7EnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn6K6h566X5py656eR5a2m5LiO5oqA5pyv5peg5pa55ZCR6IuxJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8JzIwMTLnuqforqHnrpfmnLrnp5HlrabkuI7mioDmnK%2Fml6DmlrnlkJHoi7EnfHwnLCUnICAgICBhbmQgeHpkeCBub3QgbGlrZSAnJSwnfHwn6K6h5YiSJ3x8JywlJyAgICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8JzIwMTLnuqforqHliJInfHwnLCUnICAgICBhbmQgeHpkeCBub3QgIGxpa2UgJyUsJ3x8J%2BS%2FoeaBr%2BWtpumZoid8fCcsJScgICAgICkgICAgYW5kIHNmeGd4az0n5pivJykgd2hlcmUgKHJzLXl4cnMpXD4wIG9yZGVyIGJ5IGtjZG0sanN6Z2g7YjxBQUVBQUFELy8vLy9BUUFBQUFBQUFBQU1BZ0FBQUZGVGVYTjBaVzB1UkdGMFlTd2dWbVZ5YzJsdmJqMHhMakF1TlRBd01DNHdMQ0JEZFd4MGRYSmxQVzVsZFhSeVlXd3NJRkIxWW14cFkwdGxlVlJ2YTJWdVBXSTNOMkUxWXpVMk1Ua3pOR1V3T0RrRkFRQUFBQlZUZVhOMFpXMHVSR0YwWVM1RVlYUmhWR0ZpYkdVQ0FBQUFDVmh0YkZOamFHVnRZUXRZYld4RWFXWm1SM0poYlFFQkFnQUFBQVlEQUFBQTVoWThQM2h0YkNCMlpYSnphVzl1UFNJeExqQWlJR1Z1WTI5a2FXNW5QU0oxZEdZdE1UWWlQejROQ2p4NGN6cHpZMmhsYldFZ2FXUTlJazVsZDBSaGRHRlRaWFFpSUhodGJHNXpQU0lpSUhodGJHNXpPbmh6UFNKb2RIUndPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1EQXhMMWhOVEZOamFHVnRZU0lnZUcxc2JuTTZiWE5rWVhSaFBTSjFjbTQ2YzJOb1pXMWhjeTF0YVdOeWIzTnZablF0WTI5dE9uaHRiQzF0YzJSaGRHRWlQZzBLSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKVVlXSnNaU0krRFFvZ0lDQWdQSGh6T21OdmJYQnNaWGhVZVhCbFBnMEtJQ0FnSUNBZ1BIaHpPbk5sY1hWbGJtTmxQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pMUTBSTklpQjBlWEJsUFNKNGN6cHpkSEpwYm1jaUlHMXpaR0YwWVRwMFlYSm5aWFJPWVcxbGMzQmhZMlU5SWlJZ2JXbHVUMk5qZFhKelBTSXdJaUF2UGcwS0lDQWdJQ0FnSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKTFEwMURJaUIwZVhCbFBTSjRjenB6ZEhKcGJtY2lJRzF6WkdGMFlUcDBZWEpuWlhST1lXMWxjM0JoWTJVOUlpSWdiV2x1VDJOamRYSnpQU0l3SWlBdlBnMEtJQ0FnSUNBZ0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSktVMXBIU0NJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpU2xOWVRTSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVUwdFRTaUlnZEhsd1pUMGllSE02YzNSeWFXNW5JaUJ0YzJSaGRHRTZkR0Z5WjJWMFRtRnRaWE53WVdObFBTSWlJRzFwYms5alkzVnljejBpTUNJZ0x6NE5DaUFnSUNBZ0lDQWdQSGh6T21Wc1pXMWxiblFnYm1GdFpUMGlVMHRFUkNJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpV0VZaUlIUjVjR1U5SW5oek9uTjBjbWx1WnlJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0FnSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWxwWVV5SWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVVWTktVMW9pSUhSNWNHVTlJbmh6T25OMGNtbHVaeUlnYlhOa1lYUmhPblJoY21kbGRFNWhiV1Z6Y0dGalpUMGlJaUJ0YVc1UFkyTjFjbk05SWpBaUlDOCtEUW9nSUNBZ0lDQWdJRHg0Y3pwbGJHVnRaVzUwSUc1aGJXVTlJbGhMUzBnaUlIUjVjR1U5SW5oek9uTjBjbWx1WnlJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0FnSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWxoUlFsTWlJSFI1Y0dVOUluaHpPbk4wY21sdVp5SWdiWE5rWVhSaE9uUmhjbWRsZEU1aGJXVnpjR0ZqWlQwaUlpQnRhVzVQWTJOMWNuTTlJakFpSUM4K0RRb2dJQ0FnSUNBZ0lEeDRjenBsYkdWdFpXNTBJRzVoYldVOUlrdERSMU1pSUhSNWNHVTlJbmh6T25OMGNtbHVaeUlnYlhOa1lYUmhPblJoY21kbGRFNWhiV1Z6Y0dGalpUMGlJaUJ0YVc1UFkyTjFjbk05SWpBaUlDOCtEUW9nSUNBZ0lDQWdJRHg0Y3pwbGJHVnRaVzUwSUc1aGJXVTlJa3REV0ZvaUlIUjVjR1U5SW5oek9uTjBjbWx1WnlJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0FnSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWxKVElpQjBlWEJsUFNKNGN6cGtaV05wYldGc0lpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVdWaFNVeUlnZEhsd1pUMGllSE02WkdWamFXMWhiQ0lnYlhOa1lYUmhPblJoY21kbGRFNWhiV1Z6Y0dGalpUMGlJaUJ0YVc1UFkyTjFjbk05SWpBaUlDOCtEUW9nSUNBZ0lDQWdJRHg0Y3pwbGJHVnRaVzUwSUc1aGJXVTlJbGxNSWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pDV2lJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpVFV0YVNDSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVMwdFlXU0lnZEhsd1pUMGllSE02YzNSeWFXNW5JaUJ0YzJSaGRHRTZkR0Z5WjJWMFRtRnRaWE53WVdObFBTSWlJRzFwYms5alkzVnljejBpTUNJZ0x6NE5DaUFnSUNBZ0lDQWdQSGh6T21Wc1pXMWxiblFnYm1GdFpUMGlTMU5UU2lJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpVTBaQ1dDSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVMxTllVeUlnZEhsd1pUMGllSE02YzNSeWFXNW5JaUJ0YzJSaGRHRTZkR0Z5WjJWMFRtRnRaWE53WVdObFBTSWlJRzFwYms5alkzVnljejBpTUNJZ0x6NE5DaUFnSUNBZ0lDQWdQSGh6T21Wc1pXMWxiblFnYm1GdFpUMGlVMUZUVFNJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpUmtwWVdpSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVNrTk9VaUlnZEhsd1pUMGllSE02YzNSeWFXNW5JaUJ0YzJSaGRHRTZkR0Z5WjJWMFRtRnRaWE53WVdObFBTSWlJRzFwYms5alkzVnljejBpTUNJZ0x6NE5DaUFnSUNBZ0lDQWdQSGh6T21Wc1pXMWxiblFnYm1GdFpUMGlVazRpSUhSNWNHVTlJbmh6T21SbFkybHRZV3dpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdQQzk0Y3pwelpYRjFaVzVqWlQ0TkNpQWdJQ0E4TDNoek9tTnZiWEJzWlhoVWVYQmxQZzBLSUNBOEwzaHpPbVZzWlcxbGJuUStEUW9nSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWs1bGQwUmhkR0ZUWlhRaUlHMXpaR0YwWVRwSmMwUmhkR0ZUWlhROUluUnlkV1VpSUcxelpHRjBZVHBNYjJOaGJHVTlJbnBvTFVOT0lqNE5DaUFnSUNBOGVITTZZMjl0Y0d4bGVGUjVjR1UrRFFvZ0lDQWdJQ0E4ZUhNNlkyaHZhV05sSUcxaGVFOWpZM1Z5Y3owaWRXNWliM1Z1WkdWa0lpQXZQZzBLSUNBZ0lEd3ZlSE02WTI5dGNHeGxlRlI1Y0dVK0RRb2dJRHd2ZUhNNlpXeGxiV1Z1ZEQ0TkNqd3ZlSE02YzJOb1pXMWhQZ1lFQUFBQXdnczhaR2xtWm1keU9tUnBabVpuY21GdElIaHRiRzV6T20xelpHRjBZVDBpZFhKdU9uTmphR1Z0WVhNdGJXbGpjbTl6YjJaMExXTnZiVHA0Yld3dGJYTmtZWFJoSWlCNGJXeHVjenBrYVdabVozSTlJblZ5YmpwelkyaGxiV0Z6TFcxcFkzSnZjMjltZEMxamIyMDZlRzFzTFdScFptWm5jbUZ0TFhZeElqNE5DaUFnUEU1bGQwUmhkR0ZUWlhRK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVXhJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJakFpUGcwS0lDQWdJQ0FnUEV0RFJFMCtNVE15T1RJd01ESjRNRHd2UzBORVRUNE5DaUFnSUNBZ0lEeExRMDFEUHVpbmd1aTFqK1dicmVpSnVqd3ZTME5OUXo0TkNpQWdJQ0FnSUR4S1UxcEhTRDR4TXpJd01EazhMMHBUV2tkSVBnMEtJQ0FnSUNBZ1BFcFRXRTArNVlpWTVMdVk1TGljNXFDSFBDOUtVMWhOUGcwS0lDQWdJQ0FnUEZOTFUwbys1WkdvNVlXdDU2eXNNU3d5NklxQ2UrZXNyREV0TVRMbGthaDlQQzlUUzFOS1BnMEtJQ0FnSUNBZ1BGTkxSRVErNUxpNzVxVzhOREV6S09Xa21pazhMMU5MUkVRK0RRb2dJQ0FnSUNBOFdFWStNUzQxUEM5WVJqNE5DaUFnSUNBZ0lEeGFXRk0rTWk0d0xUQXVNRHd2V2xoVFBnMEtJQ0FnSUNBZ1BGRlRTbE5hUGpBeExURXlQQzlSVTBwVFdqNE5DaUFnSUNBZ0lEeFlTMHRJUGlneU1ERTBMVEl3TVRVdE1pa3RNVE15T1RJd01ESjRNQzB4TXpJd01Ea3RNVHd2V0V0TFNENE5DaUFnSUNBZ0lEeFlVVUpUUHVhNWx1V0ZpZWFnb2VXTXVqd3ZXRkZDVXo0TkNpQWdJQ0FnSUR4TFEwZFRQdWlIcXVlRXR1ZW5rZVd0cHVleHV6d3ZTME5IVXo0TkNpQWdJQ0FnSUR4TFExaGFQdVM3dSttQWlUd3ZTME5ZV2o0TkNpQWdJQ0FnSUR4U1V6NHhNRFU4TDFKVFBnMEtJQ0FnSUNBZ1BGbFlVbE0rTVRBMFBDOVpXRkpUUGcwS0lDQWdJQ0FnUEV0TFdGays1WWFjNWEybTZabWlQQzlMUzFoWlBnMEtJQ0FnSUNBZ1BFcERUbEkrNktlQzZMV1A1WnV0NkltNjVhMm1mT1M0cmVXYnZlV0duT1M0bXVXSHV1ZUppT2Vrdm56cG1ZamxqNUhtbzZQamdJSHBnNjNudTdUbW1JNThNand2U2tOT1VqNE5DaUFnSUNBZ0lEeFNUajR4UEM5U1RqNE5DaUFnSUNBOEwxUmhZbXhsUGcwS0lDQWdJRHhVWVdKc1pTQmthV1ptWjNJNmFXUTlJbFJoWW14bE1pSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXhJajROQ2lBZ0lDQWdJRHhMUTBSTlBqVTVNVGt5TURBeGVEQThMMHREUkUwK0RRb2dJQ0FnSUNBOFMwTk5RejdscEpybHFwTGt2WlBsaUxia3Zaem1pb0Rtbks4OEwwdERUVU0rRFFvZ0lDQWdJQ0E4U2xOYVIwZytNVFkwTURBNVBDOUtVMXBIU0Q0TkNpQWdJQ0FnSUR4S1UxaE5QdVdRdE9hVmp6d3ZTbE5ZVFQ0TkNpQWdJQ0FnSUR4VFMxTktQdVdScU9TNmpPZXNyRFVzTnVpS2dudm5yS3d4TFRFeTVaR29mVHd2VTB0VFNqNE5DaUFnSUNBZ0lEeFRTMFJFUHVtU24rYTF0K2FsdkRBMk1EQTRQQzlUUzBSRVBnMEtJQ0FnSUNBZ1BGaEdQakV1TlR3dldFWStEUW9nSUNBZ0lDQThXbGhUUGpJdU1DMHdMakE4TDFwWVV6NE5DaUFnSUNBZ0lEeFJVMHBUV2o0d01TMHhNand2VVZOS1UxbytEUW9nSUNBZ0lDQThXRXRMU0Q0b01qQXhOQzB5TURFMUxUSXBMVFU1TVRreU1EQXhlREF0TVRZME1EQTVMVEU4TDFoTFMwZytEUW9nSUNBZ0lDQThXRkZDVXo3bXVaYmxoWW5tb0tIbGpMbzhMMWhSUWxNK0RRb2dJQ0FnSUNBOFMwTkhVejdvaDZybmhMYm5wNUhscmFibnNiczhMMHREUjFNK0RRb2dJQ0FnSUNBOFMwTllXajdrdTd2cGdJazhMMHREV0ZvK0RRb2dJQ0FnSUNBOFVsTStOekE4TDFKVFBnMEtJQ0FnSUNBZ1BGbFlVbE0rTmpjOEwxbFlVbE0rRFFvZ0lDQWdJQ0E4UzB0WVdUN2xycDdwcW96bWxabmxyYWJwZzZnOEwwdExXRmsrRFFvZ0lDQWdJQ0E4U2tOT1VqNThmSHc4TDBwRFRsSStEUW9nSUNBZ0lDQThVazQrTWp3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdQQzlPWlhkRVlYUmhVMlYwUGcwS1BDOWthV1ptWjNJNlpHbG1abWR5WVcwK0N3PT0%2BOz4%2BOz47bDxpPDE%2BOz47bDx0PDtsPGk8MT47aTwzPjtpPDU%2BO2k8Nz47aTwxMT47aTwxMz47aTwxNT47aTwxNz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Mjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8RW5hYmxlZDs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8RW5hYmxlZDs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8RW5hYmxlZDs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8RW5hYmxlZDs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47QDA8Ozs7Ozs7Ozs7Ozs7Ozs7O0AwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs7Oz47Oz47Ozs7Ozs7Ozs%2BOzs%2BO3Q8cDxwPGw8UXVlcnk7UXVlcnlQYXJhbXM7ZHRSZWNvcmRzOz47bDxzZWxlY3QgYi54a2toLGIua2NtYyxiLmpzeG0sYi54ZixiLnp4cyxiLnFzanN6LGIuc2tzaixiLnNrZGQsYS5qY3lkLGIuYnosYi5rY2RtLGIuanN6Z2gsYi54cWJzLGIua2NncyxiLm1remgsYi5rY3h6LCcnc2ZieCwgYi5qY21jfHwnfCd8fGIuY2JzfHwnfCd8fGIuenp8fCd8J3x8Yi5iYiBqY25yIGZyb20geHN4a2JuIGEseHhranhyd2IgYiAgd2hlcmUgYS54a2toPWIueGtraCAgYW5kIGEueGg9OnBhcmFtc3RyMSBhbmQgYS54a2toIGxpa2UgOnBhcmFtc3RyMiAgYW5kIHNmeGd4az0n5pivJyBvcmRlciBieSBiLmtjZ3MsYS54a2toO0A8MjAxMjExNjIxMTMzOygyMDE0LTIwMTUtMiktJTs%2BO2I8QUFFQUFBRC8vLy8vQVFBQUFBQUFBQUFNQWdBQUFGRlRlWE4wWlcwdVJHRjBZU3dnVm1WeWMybHZiajB4TGpBdU5UQXdNQzR3TENCRGRXeDBkWEpsUFc1bGRYUnlZV3dzSUZCMVlteHBZMHRsZVZSdmEyVnVQV0kzTjJFMVl6VTJNVGt6TkdVd09Ea0ZBUUFBQUJWVGVYTjBaVzB1UkdGMFlTNUVZWFJoVkdGaWJHVUNBQUFBQ1ZodGJGTmphR1Z0WVF0WWJXeEVhV1ptUjNKaGJRRUJBZ0FBQUFZREFBQUEzUkU4UDNodGJDQjJaWEp6YVc5dVBTSXhMakFpSUdWdVkyOWthVzVuUFNKMWRHWXRNVFlpUHo0TkNqeDRjenB6WTJobGJXRWdhV1E5SWs1bGQwUmhkR0ZUWlhRaUlIaHRiRzV6UFNJaUlIaHRiRzV6T25oelBTSm9kSFJ3T2k4dmQzZDNMbmN6TG05eVp5OHlNREF4TDFoTlRGTmphR1Z0WVNJZ2VHMXNibk02YlhOa1lYUmhQU0oxY200NmMyTm9aVzFoY3kxdGFXTnliM052Wm5RdFkyOXRPbmh0YkMxdGMyUmhkR0VpUGcwS0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSlVZV0pzWlNJK0RRb2dJQ0FnUEhoek9tTnZiWEJzWlhoVWVYQmxQZzBLSUNBZ0lDQWdQSGh6T25ObGNYVmxibU5sUGcwS0lDQWdJQ0FnSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKWVMwdElJaUIwZVhCbFBTSjRjenB6ZEhKcGJtY2lJRzF6WkdGMFlUcDBZWEpuWlhST1lXMWxjM0JoWTJVOUlpSWdiV2x1VDJOamRYSnpQU0l3SWlBdlBnMEtJQ0FnSUNBZ0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSkxRMDFESWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pLVTFoTklpQjBlWEJsUFNKNGN6cHpkSEpwYm1jaUlHMXpaR0YwWVRwMFlYSm5aWFJPWVcxbGMzQmhZMlU5SWlJZ2JXbHVUMk5qZFhKelBTSXdJaUF2UGcwS0lDQWdJQ0FnSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKWVJpSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVdsaFRJaUIwZVhCbFBTSjRjenB6ZEhKcGJtY2lJRzF6WkdGMFlUcDBZWEpuWlhST1lXMWxjM0JoWTJVOUlpSWdiV2x1VDJOamRYSnpQU0l3SWlBdlBnMEtJQ0FnSUNBZ0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSlJVMHBUV2lJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpVTB0VFNpSWdkSGx3WlQwaWVITTZjM1J5YVc1bklpQnRjMlJoZEdFNmRHRnlaMlYwVG1GdFpYTndZV05sUFNJaUlHMXBiazlqWTNWeWN6MGlNQ0lnTHo0TkNpQWdJQ0FnSUNBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVUwdEVSQ0lnZEhsd1pUMGllSE02YzNSeWFXNW5JaUJ0YzJSaGRHRTZkR0Z5WjJWMFRtRnRaWE53WVdObFBTSWlJRzFwYms5alkzVnljejBpTUNJZ0x6NE5DaUFnSUNBZ0lDQWdQSGh6T21Wc1pXMWxiblFnYm1GdFpUMGlTa05aUkNJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpUWxvaUlIUjVjR1U5SW5oek9uTjBjbWx1WnlJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0FnSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWt0RFJFMGlJSFI1Y0dVOUluaHpPbk4wY21sdVp5SWdiWE5rWVhSaE9uUmhjbWRsZEU1aGJXVnpjR0ZqWlQwaUlpQnRhVzVQWTJOMWNuTTlJakFpSUM4K0RRb2dJQ0FnSUNBZ0lEeDRjenBsYkdWdFpXNTBJRzVoYldVOUlrcFRXa2RJSWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pZVVVKVElpQjBlWEJsUFNKNGN6cHpkSEpwYm1jaUlHMXpaR0YwWVRwMFlYSm5aWFJPWVcxbGMzQmhZMlU5SWlJZ2JXbHVUMk5qZFhKelBTSXdJaUF2UGcwS0lDQWdJQ0FnSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKTFEwZFRJaUIwZVhCbFBTSjRjenB6ZEhKcGJtY2lJRzF6WkdGMFlUcDBZWEpuWlhST1lXMWxjM0JoWTJVOUlpSWdiV2x1VDJOamRYSnpQU0l3SWlBdlBnMEtJQ0FnSUNBZ0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSk5TMXBJSWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pMUTFoYUlpQjBlWEJsUFNKNGN6cHpkSEpwYm1jaUlHMXpaR0YwWVRwMFlYSm5aWFJPWVcxbGMzQmhZMlU5SWlJZ2JXbHVUMk5qZFhKelBTSXdJaUF2UGcwS0lDQWdJQ0FnSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKVFJrSllJaUIwZVhCbFBTSjRjenB6ZEhKcGJtY2lJRzF6WkdGMFlUcDBZWEpuWlhST1lXMWxjM0JoWTJVOUlpSWdiV2x1VDJOamRYSnpQU0l3SWlBdlBnMEtJQ0FnSUNBZ0lDQThlSE02Wld4bGJXVnVkQ0J1WVcxbFBTSktRMDVTSWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0pTVGlJZ2RIbHdaVDBpZUhNNlpHVmphVzFoYkNJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0E4TDNoek9uTmxjWFZsYm1ObFBnMEtJQ0FnSUR3dmVITTZZMjl0Y0d4bGVGUjVjR1UrRFFvZ0lEd3ZlSE02Wld4bGJXVnVkRDROQ2lBZ1BIaHpPbVZzWlcxbGJuUWdibUZ0WlQwaVRtVjNSR0YwWVZObGRDSWdiWE5rWVhSaE9rbHpSR0YwWVZObGREMGlkSEoxWlNJZ2JYTmtZWFJoT2t4dlkyRnNaVDBpZW1ndFEwNGlQZzBLSUNBZ0lEeDRjenBqYjIxd2JHVjRWSGx3WlQ0TkNpQWdJQ0FnSUR4NGN6cGphRzlwWTJVZ2JXRjRUMk5qZFhKelBTSjFibUp2ZFc1a1pXUWlJQzgrRFFvZ0lDQWdQQzk0Y3pwamIyMXdiR1Y0Vkhsd1pUNE5DaUFnUEM5NGN6cGxiR1Z0Wlc1MFBnMEtQQzk0Y3pwelkyaGxiV0UrQmdRQUFBQ0FBVHhrYVdabVozSTZaR2xtWm1keVlXMGdlRzFzYm5NNmJYTmtZWFJoUFNKMWNtNDZjMk5vWlcxaGN5MXRhV055YjNOdlpuUXRZMjl0T25odGJDMXRjMlJoZEdFaUlIaHRiRzV6T21ScFptWm5jajBpZFhKdU9uTmphR1Z0WVhNdGJXbGpjbTl6YjJaMExXTnZiVHA0Yld3dFpHbG1abWR5WVcwdGRqRWlJQzgrQ3c9PT47Pj47PjtsPGk8MT47PjtsPHQ8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDE%2BO2k8Mz47aTw1PjtpPDc%2BO2k8MTE%2BO2k8MTM%2BO2k8MTU%2BO2k8MTc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDA7Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjs%2BPjs%2BPjt0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8O2w8aTwzPjs%2BO2w8dDxAMDw7Ozs7Ozs7Oz47Oz47Pj47Pj47Pj47bDxrY21jR3JpZDpfY3RsMjp4aztrY21jR3JpZDpfY3RsMjpqYztrY21jR3JpZDpfY3RsMzp4aztrY21jR3JpZDpfY3RsMzpqYzs%2BPqNmjIsdfa%2BlQU%2BbZ61PiexzBBMs&ddl_kcxz=&ddl_ywyl=%CE%DE&ddl_kcgs=&ddl_xqbs=1&ddl_sksj=&TextBox1=&dpkcmcGrid%3AtxtChoosePage=1&dpkcmcGrid%3AtxtPageSize=113");
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
	 * 解析公选课课程名
	 */
	public static Set<OptionalCourse> parseOpenSelectSource(String html){
		Set<String> sourceName = new HashSet<String>();
		Set<OptionalCourse> courses = new HashSet<OptionalCourse>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("kcmcGrid");
		Elements trs = table.getElementsByTag("tr");
		boolean flag = true;
		for (Element tr:trs) {
			if(flag){             //省去第一行
				flag = false;
				continue;
			}
			String[] temp =tr.text().split(" ");
		
		
			if(!sourceName.contains(temp[0])){
				sourceName.add(temp[0]);
			//	System.out.println(temp[0]+temp[2]+temp[3].substring(0, temp[3].indexOf("{"))+temp[4]);
				OptionalCourse c = new OptionalCourse(temp[0], temp[2], temp[3].substring(0, temp[3].indexOf("{")), temp[4]);
				courses.add(c);
			}
		
		}
		return courses;
	}
	/**
	 * 解析体育课课程名
	 * @throws Exception 
	 */
	public static Set<String> parseTyForName(String tiyuurl,List<String> lists) throws Exception{
		Set<String> tyName = new HashSet<String>();
		for (int i = 0; i < lists.size(); i++) {
			String html = CreeperInfos.getTy(tiyuurl,lists.get(i));
			Document doc = Jsoup.parse(html);
			Element table = doc.getElementById("kcmcGrid");
			Elements trs = table.getElementsByTag("tr");
			boolean flag = true;
			for (Element tr:trs) {
				if(flag){             //省去第一行
					flag = false;
					continue;
				}
				String[] temp =tr.text().split(" ");
				if(!tyName.contains(temp[0])){
					tyName.add(temp[0]);
					System.out.println(temp[0]);
				}
			}
		}
		
		return tyName;
	}
	/**
	 * 解析体育课的上课时间
	 * @throws IOException 
	 */
	public static List<String> parseTY(String html) throws IOException{
		List<String> lists = new ArrayList<String>();
		Document doc = Jsoup.parse(html);
		Element select = doc.getElementById("kj");//获取课程表对应的标签select
		Elements options = select.getElementsByTag("option");
		boolean flag = true;
		for(Element option:options){
			if(flag){             //省去第一行
				flag = false;
				continue;
			}
			String time = option.text();
			time = URLEncoder.encode(time,"gb2312");
			lists.add(time);
		}
		return lists;
	}
	/**
	 * 解析成绩
	 * @param html
	 */
	public static List<Grade> parseScore(String html){
		List<Grade> grades = new ArrayList<Grade>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("Datagrid1");//获取课程表对应的标签table
		Elements trs = table.getElementsByTag("tr");
		boolean flag = true;
		for(Element tr:trs){
			if(flag){             //省去第一行
				flag = false;
				continue;
			}
			String[] content = tr.text().split(" ");
			String year = content[0];//学年
			String term = content[1];//学期
			String courseName = content[3];//课程名
			String courseType = content[4];//课程归属 限选，必修，任选
			String credit = content[6];//学分
			String gradepoint = content[7];//绩点
			String score = content[8];//成绩
			//String makeupscore = content[8];//补考成绩
			//String rebuildscore = content[9];//重修
			
			
			int systemYear = Integer.parseInt(DateUtil.getYear());//当前年份
			if(systemYear%2==0){
				systemYear+=1;
			}
			if(year.contains(String.valueOf(systemYear))){
				Grade grade = new Grade(year, term, courseName, courseType, credit, gradepoint, score);
				grades.add(grade);
				//System.out.println("学年：" +year+" "+" 学期："+term+" "+" 课程名:"+courseName+"     "+"课程归属:"+courseType+" "+"学分:"+credit+" "+"绩点"+gradepoint+" "+"成绩:"+score+" ");
			}
		}
		return grades;
	}
	/**
	 * 解析课程表
	 * @param html
	 */
	public static List<Course> parseSchedule(String html){
		List<Course> courseList = new ArrayList<Course>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("Table1");//获取课程表对应的标签table
		Elements trs = table.getElementsByTag("tr");
		for (int i = 2; i < trs.size();i+=2) {
			  int row = (i-1); //第几节
			  Element  tr = trs.get(i);
			  Elements tds = tr.getElementsByTag("td");
     		  for (int j = 0; j < tds.size(); j++) {
     			  if(tds.get(j).text().length()>10){
     				  String text = tds.get(j).text();
     				  String[] content = text.split(" ");
     				  for(int z =0 ;z<content.length;z+=4){
	     					 String courseName = content[z];//课程名
	        				 String weekday = content[z+1].substring(0,content[z+1].indexOf("第")); //星期
	        				 int col =0 ;//星期
	        				 String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
	        				 for (int k = 0; k < weeks.length; k++) {
		   						if(weekday.equals(weeks[k])){
		   							col = k;
		   							break;
		   						}
	        				  }
	        				 
	        				  String teacher = content[z+2];//老师  
	        				  if(teacher.contains("(")){
	        					  teacher = teacher.substring(0,teacher.lastIndexOf("("));  // 邹阿金（邹阿金）转成 邹阿金 
	        				  }
	        				  
	        				  String address = content[z+3].substring(0,content[z+3].lastIndexOf("(")); //上课地点  主楼609(多)转成  主楼609
	        				  String time = content[z+1];
	        				  String week =time.substring(time.indexOf("{")+1,time.lastIndexOf("}"));//周数
	        				  if(week.contains("|")){
	        					  week = week.substring(week.indexOf("第")+1,week.indexOf("周"));
	        					  String[] ranges = week.split("-");
	        					  week ="";
	        					  StringBuilder temp = new StringBuilder();
	        					  int beginIndex = Integer.parseInt(ranges[0]);
	        					  int lastIndex = Integer.parseInt(ranges[1]);
	        					  for (; beginIndex<=lastIndex; beginIndex+=2) {
	        						  temp.append(beginIndex);
	        						  if(beginIndex!=lastIndex){
	        							 temp.append('|');
	        						  }
	   						  }
	        					  week = temp.toString();
	        				  }else{
	        					 week = week.substring(week.indexOf("第")+1,week.indexOf("周"));
	        				  }
	        			Course course = new Course(content[z], week, col, row, address, teacher);	  
        				//System.out.println("名："+ content[z]+" "+"第 "+row+"节："+"星期："+col+  "周数："+week+"  老师："+teacher+" 上课地点："+address);
	        			courseList.add(course);
     				  }
     				  
     				 
     			  }
				
			 }
     		// System.out.println();
		}
		return courseList;
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
		if(tonggous == null){
			return null;
		}
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
			   //System.out.println(linkText+":"+truelinkHref);
			   urlMap.put(linkText, truelinkHref);
		  }
	     }
		return urlMap;
	}
	//用户调用，返回已选的公选课
	public List<OptionalCourse> getYixuanGongxuangke() {
		List<OptionalCourse> courses =null ;
		try{
			String response = getopenSelectSource(baseURL+subURL.get("校公选课"));
		    courses = parseYixuanGongxuangke(response);
		}catch(Exception e){
			e.printStackTrace();
		}
		return courses;
	}
	/**
	 * 返回已选的公选课
	 * @param args
	 * @throws Exception
	 */
	public static  List<OptionalCourse> parseYixuanGongxuangke(String html) throws Exception{
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("DataGrid2");
		Elements trs = table.getElementsByTag("tr");
		boolean flag = true;
		List<OptionalCourse> courses = new ArrayList<OptionalCourse>();
		for (Element tr:trs) {
			if(flag){             //省去第一行
				flag = false;
				continue;
			}
			String[] temp =tr.text().split(" ");
			OptionalCourse course = new OptionalCourse(temp[0], temp[1], temp[6], temp[7]);
			System.out.println("选修课名："+ temp[0]+"    教师："+temp[1]+"  上课时间："+temp[6]+"  课程属性："+temp[7]);
			courses.add(course);
		}
			
		return courses;
	}
	
	/**
	 * 抓取本学年，本学期的主楼和科技楼的有课的教室
	 * @param xuenian :学年
	 * @param xueqi：学期 1或者2
	 * @return
	 */
	public static  List<FreeCourseMain> getTotalUnFreeCourse(String xuenian,String xueqi) {
		List<FreeCourseMain> list = new ArrayList<FreeCourseMain>();
		try {
			String response = getFreeCourses();
			Map<String,String> map = parseIndexHtml1(response);
			String content = getKonyujiaoshi(baseURL+map.get("空教室查询"),"01203",xuenian,xueqi);
	    	List<ReferName> referNames = getValue(content);
	    	list= parseForFreeCourses(referNames, baseURL+map.get("空教室查询"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return list;
	}
	public static void main(String[] args) throws Exception {
         CreeperInfos infos = new CreeperInfos("201211621310","889798guozaopeng");
         List<Course> courses = infos.getStudentCource();
         
         System.out.println("个人课表："+subURL.get("学生个人课表"));;
         System.out.println("班级课表查询："+subURL.get("班级课表查询"));;
         /* List<Grade> grades = infos.getStudentGrade();
       
         for(Grade grade : grades){
        	 System.out.println(grade.getCourseName());
         }*/
         //201211621310  889798guozaopeng
		 //String response = getFreeCourses();
		//System.out.println(response);
		//Map<String,String> map = parseIndexHtml1(response);
	  // System.out.println(map.get("空教室查询"));
		//System.out.println(baseURL);
    	
    	//System.out.println(content);
		//parseKeshi(content);
		//getValue(content);
		//String content = getKonyujiaoshi(baseURL+map.get("空教室查询"),"01203","2014-2015","2");
    	//List<ReferName> referNames = getValue(content);
    	//List<FreeCourseMain> list= parseForFreeCourses(referNames, baseURL+map.get("空教室查询"));
	}
	
	/**
	 * 获取所有有课的课室的详细信息
	 * @param referNames
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<FreeCourseMain> parseForFreeCourses(List<ReferName> referNames,String path) throws Exception{
		List<FreeCourseMain> reCourses =  new ArrayList<FreeCourseMain>();
		for(ReferName refName : referNames){
			String content = getKonyujiaoshi(path,refName.getReferValue(),"2014-2015","2");
			parseKeshi(content,reCourses,refName.getClassName());
		}
		return reCourses;
	}
	

	
	/**
	 * 解析
	 * @description
	 * @param html
	 */
	private static List<FreeCourseMain> parseKeshi(String html,List<FreeCourseMain> list,String truename){
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("Datagrid3");//获取课程表对应的标签table
		Elements trs = table.getElementsByTag("tr");
		boolean flag = true;
		for(Element tr:trs){
			if(flag){             //省去第一行
				flag = false;
				continue;
			}
			String[] c = tr.text().split(" ");
		   FreeCourseMain cMain = new FreeCourseMain(c[0],c[1],c[2], c[3], c[4],truename);
		  //  System.out.println(c[0]+"--"+c[1]+"--"+c[2]+"--"+c[3]+"--"+c[4]);
		   System.out.println(cMain);
		   list.add(cMain);
		}
		return list;
	}
	
	/**
	 * 获取该教室在这个学期的空余课室
	 * @param path   
	 * @param className ：教室名
	 * @param xueqi ：当前时间
	 * @return   ：页面
	 * @throws Exception
	 */
	private static String getKonyujiaoshi(String path,String className,String xuenian,String xueqi) throws Exception {
		URL url = new URL(path);//
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Referer",path);
		connection.setRequestProperty("Cookie","tabId=ext-comp-1004");
		connection.setRequestProperty("Content-Length", "78042");
		connection.connect();
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.write("__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=dDwxMTUyNzkwNTI7dDxwPGw8anN5eW1za2c7ZHF4bnhxO2pzeXV5ZGR5Oz47bDwwMDA7MjAxNC0yMDE1MjtcZTs%2BPjtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8OT47aTwxMT47aTwxND47aTwxOD47aTwyMT47aTwyND47aTwyNj47aTwzOD47aTw0MD47aTw0Mj47aTw0OD47aTw1MD47aTw1ND47aTw1Nj47aTw2Mj47aTw2ND47PjtsPHQ8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4cW1jO3hxZG07Pj47Pjt0PGk8ND47QDxcZTvmuZblhYnmoKHljLo75rW35ruo5qCh5Yy6O%2BmcnuWxseagoeWMujs%2BO0A8XGU7MTsyOzM7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxqc2xiO2pzbGI7Pj47Pjt0PGk8MTA%2BO0A8XGU75aSa5aqS5L2T5pWZ5a6kO%2BWFtuWugzvnkLTmiL876K6%2B6K6h5pWZ5a6kO%2BWunumqjOWupDvoibrmnK%2Fnsbs76K%2Bt6Z%2Bz5a6e6K6t5a6kO%2Bivremfs%2BWupDvov5DliqjlnLo7PjtAPFxlO%2BWkmuWqkuS9k%2BaVmeWupDvlhbblroM755C05oi%2FO%2BiuvuiuoeaVmeWupDvlrp7pqozlrqQ76Im65pyv57G7O%2Bivremfs%2BWunuiureWupDvor63pn7PlrqQ76L%2BQ5Yqo5Zy6Oz4%2BO2w8aTwwPjs%2BPjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDIwMTQtMjAxNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Mjs%2BPjs%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxueXI7eHE7Pj47Pjt0PGk8NDY%2BO0A8MjAxNS0wNS0yNjsyMDE1LTA1LTI3OzIwMTUtMDUtMjg7MjAxNS0wNS0yOTsyMDE1LTA1LTMwOzIwMTUtMDUtMzE7MjAxNS0wNi0wMTsyMDE1LTA2LTAyOzIwMTUtMDYtMDM7MjAxNS0wNi0wNDsyMDE1LTA2LTA1OzIwMTUtMDYtMDY7MjAxNS0wNi0wNzsyMDE1LTA2LTA4OzIwMTUtMDYtMDk7MjAxNS0wNi0xMDsyMDE1LTA2LTExOzIwMTUtMDYtMTI7MjAxNS0wNi0xMzsyMDE1LTA2LTE0OzIwMTUtMDYtMTU7MjAxNS0wNi0xNjsyMDE1LTA2LTE3OzIwMTUtMDYtMTg7MjAxNS0wNi0xOTsyMDE1LTA2LTIwOzIwMTUtMDYtMjE7MjAxNS0wNi0yMjsyMDE1LTA2LTIzOzIwMTUtMDYtMjQ7MjAxNS0wNi0yNTsyMDE1LTA2LTI2OzIwMTUtMDYtMjc7MjAxNS0wNi0yODsyMDE1LTA2LTI5OzIwMTUtMDYtMzA7MjAxNS0wNy0wMTsyMDE1LTA3LTAyOzIwMTUtMDctMDM7MjAxNS0wNy0wNDsyMDE1LTA3LTA1OzIwMTUtMDctMDY7MjAxNS0wNy0wNzsyMDE1LTA3LTA4OzIwMTUtMDctMDk7MjAxNS0wNy0xMDs%2BO0A8MjEyOzMxMjs0MTI7NTEyOzYxMjs3MTI7MTEzOzIxMzszMTM7NDEzOzUxMzs2MTM7NzEzOzExNDsyMTQ7MzE0OzQxNDs1MTQ7NjE0OzcxNDsxMTU7MjE1OzMxNTs0MTU7NTE1OzYxNTs3MTU7MTE2OzIxNjszMTY7NDE2OzUxNjs2MTY7NzE2OzExNzsyMTc7MzE3OzQxNzs1MTc7NjE3OzcxNzsxMTg7MjE4OzMxODs0MTg7NTE4Oz4%2BO2w8aTwwPjs%2BPjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bnlyO3hxOz4%2BOz47dDxpPDQ2PjtAPDIwMTUtMDUtMjY7MjAxNS0wNS0yNzsyMDE1LTA1LTI4OzIwMTUtMDUtMjk7MjAxNS0wNS0zMDsyMDE1LTA1LTMxOzIwMTUtMDYtMDE7MjAxNS0wNi0wMjsyMDE1LTA2LTAzOzIwMTUtMDYtMDQ7MjAxNS0wNi0wNTsyMDE1LTA2LTA2OzIwMTUtMDYtMDc7MjAxNS0wNi0wODsyMDE1LTA2LTA5OzIwMTUtMDYtMTA7MjAxNS0wNi0xMTsyMDE1LTA2LTEyOzIwMTUtMDYtMTM7MjAxNS0wNi0xNDsyMDE1LTA2LTE1OzIwMTUtMDYtMTY7MjAxNS0wNi0xNzsyMDE1LTA2LTE4OzIwMTUtMDYtMTk7MjAxNS0wNi0yMDsyMDE1LTA2LTIxOzIwMTUtMDYtMjI7MjAxNS0wNi0yMzsyMDE1LTA2LTI0OzIwMTUtMDYtMjU7MjAxNS0wNi0yNjsyMDE1LTA2LTI3OzIwMTUtMDYtMjg7MjAxNS0wNi0yOTsyMDE1LTA2LTMwOzIwMTUtMDctMDE7MjAxNS0wNy0wMjsyMDE1LTA3LTAzOzIwMTUtMDctMDQ7MjAxNS0wNy0wNTsyMDE1LTA3LTA2OzIwMTUtMDctMDc7MjAxNS0wNy0wODsyMDE1LTA3LTA5OzIwMTUtMDctMTA7PjtAPDIxMjszMTI7NDEyOzUxMjs2MTI7NzEyOzExMzsyMTM7MzEzOzQxMzs1MTM7NjEzOzcxMzsxMTQ7MjE0OzMxNDs0MTQ7NTE0OzYxNDs3MTQ7MTE1OzIxNTszMTU7NDE1OzUxNTs2MTU7NzE1OzExNjsyMTY7MzE2OzQxNjs1MTY7NjE2OzcxNjsxMTc7MjE3OzMxNzs0MTc7NTE3OzYxNzs3MTc7MTE4OzIxODszMTg7NDE4OzUxODs%2BPjtsPGk8MD47Pj47Oz47dDx0PDt0PGk8MT47QDzkuow7PjtAPDI7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDw7dDxpPDE%2BO0A85Y%2BMOz47QDzlj4w7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx6d3NqZDtzajs%2BPjs%2BO3Q8aTwxMD47QDznrKwxLDLoioI756ysMyw06IqCO%2BesrDUsNuiKgjvnrKw3LDjoioI756ysOSwxMOiKgjvkuIrljYg75LiL5Y2IO%2BaZmuS4ijvnmb3lpKk75pW05aSpOz47QDwnMSd8JzEnLCcwJywnMCcsJzAnLCcwJywnMCcsJzAnLCcwJywnMCc7JzInfCcwJywnMycsJzAnLCcwJywnMCcsJzAnLCcwJywnMCcsJzAnOyczJ3wnMCcsJzAnLCc1JywnMCcsJzAnLCcwJywnMCcsJzAnLCcwJzsnNCd8JzAnLCcwJywnMCcsJzcnLCcwJywnMCcsJzAnLCcwJywnMCc7JzUnfCcwJywnMCcsJzAnLCcwJywnOScsJzAnLCcwJywnMCcsJzAnOyc2J3wnMScsJzMnLCcwJywnMCcsJzAnLCcwJywnMCcsJzAnLCcwJzsnNyd8JzAnLCcwJywnNScsJzcnLCcwJywnMCcsJzAnLCcwJywnMCc7JzgnfCcwJywnMCcsJzAnLCcwJywnOScsJzAnLCcwJywnMCcsJzAnOyc5J3wnMScsJzMnLCc1JywnNycsJzAnLCcwJywnMCcsJzAnLCcwJzsnMTAnfCcxJywnMycsJzUnLCc3JywnOScsJzAnLCcwJywnMCcsJzAnOz4%2BO2w8aTwwPjs%2BPjs7Pjt0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8O2w8aTwxPjs%2BO2w8dDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs7Pjs%2BPjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8MTA%2BO0A8MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7MjAxMy0yMDE0OzIwMTQtMjAxNTsyMDE1LTIwMTY7MjAxNi0yMDE3Oz47QDwyMDA3LTIwMDg7MjAwOC0yMDA5OzIwMDktMjAxMDsyMDEwLTIwMTE7MjAxMS0yMDEyOzIwMTItMjAxMzsyMDEzLTIwMTQ7MjAxNC0yMDE1OzIwMTUtMjAxNjsyMDE2LTIwMTc7Pj47Pjs7Pjt0PEAwPDtAMDw7Ozs7Ozs7Ozs7Ozs7Ozs7O0AwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs7Oz47Oz47Ozs7Ozs7Ozs%2BOzs%2BO3Q8O2w8aTwxPjs%2BO2w8dDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs7Pjs%2BPjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8anNtYztqc2JoOz4%2BOz47dDxpPDY2Nz47QDzkuLvmpbwxMDAxO%2BS4u%2BalvDEwMDY75Li75qW8MjAyKOWkmik75Li75qW8MjAzKOWkmik75Li75qW8MjA0KOWkmik75Li75qW8MjA1KOWkmik75Li75qW8MjA4KOWkmik75Li75qW8MjA5KOWkmik75Li75qW8MjEwKOWkmik75Li75qW8MjExKOWkmik75Li75qW8MjE3KOWkmik75Li75qW8MjE4KOWkmik75Li75qW8MjE5KOWkmik75Li75qW8MjIyKOWkmik75Li75qW8MjIzKOWkmik75Li75qW8MjI0KOWkmik75Li75qW8MjI1KOWkmik75Li75qW8MjI2KOWkmik75Li75qW8MjI3KOWkmik75Li75qW8MjI4KOWkmik75Li75qW8MjMwKOWkmik75Li75qW8MjMxKOWkmik75Li75qW8MzAxKOWkmik75Li75qW8MzAyKOWkmik75Li75qW8MzAzKOWkmik75Li75qW8MzA0KOWkmik75Li75qW8MzA1KOWkmik75Li75qW8MzA4KOWkmik75Li75qW8MzA5KOWkmik75Li75qW8MzEwKOWkmik76L2u5py65a6e6aqM5qW8MTAxO%2Bi9ruacuuWunumqjOalvDEwMjvova7mnLrlrp7pqozmpbwxMDM76L2u5py65a6e6aqM5qW8MTA0O%2Bi9ruacuuWunumqjOalvDEwNTvkuLvmpbwzMTEo5aSaKTvkuLvmpbwzMTI75Li75qW8MzEzKOWkmik75Li75qW8MzE2KOWkmik75Li75qW8MzE3KOWkmik75Li75qW8MzE4KOWkmik75Li75qW8MzE5KOWkmik76L2u5py65a6e6aqM5qW8MjAxO%2Bi9ruacuuWunumqjOalvDIwMjvova7mnLrlrp7pqozmpbwyMDM75Li75qW8MzIyKOWkmik75Li75qW8MzIzKOWkmik75Li75qW8MzI0KOWkmik75Li75qW8MzI1KOWkmik75Li75qW8MzI2KOWkmik75Li75qW8MzI3KOWkmik75Li75qW8MzI4KOWkmik75Li75qW8MzI5KOWkmik75Li75qW8MzMwKOWkmik75Li75qW8NDAxKOWkmik75Li75qW8NDAyKOWkmik75Li75qW8NDAzKOWkmik75Li75qW8NDA0KOWkmik75Li75qW8NDA1KOWkmik75Li75qW8NDA4KOWkmik75Li75qW8NDA5KOWkmik75Li75qW8NDEwKOWkmik75Li75qW8NDExKOWkmik75Li75qW8NDEyKOWkmik75Li75qW8NDEzKOWkmik75Li75qW8NDE2KOWkmik75Li75qW8NDE3KOWkmik75Li75qW8NDE4KOWkmik75Li75qW8NDE5KOWkmik75Li75qW8NDIyKOWkmik75Li75qW8NDIzKOWkmik75Li75qW8NDI0KOWkmik75Li75qW8NDI1KOWkmik75Li75qW8NDI2KOWkmik756ys5LiJ5a6e6aqM5qW8QTEwMS0xO%2BesrOS4ieWunumqjOalvEExMDEtMjvnrKzkuInlrp7pqozmpbxBMTAyO%2BesrOS4ieWunumqjOalvEExMDQ756ys5LiJ5a6e6aqM5qW8QTIwMjvnrKzkuInlrp7pqozmpbxBMjA0O%2BesrOS4ieWunumqjOalvEEyMDc756ys5LiJ5a6e6aqM5qW8QTIwOTvnrKzkuInlrp7pqozmpbxBMjExO%2BesrOS4ieWunumqjOalvEEyMTI756ys5LiJ5a6e6aqM5qW8QTIxMzvnrKzkuInlrp7pqozmpbxCMjA5O%2BesrOS4ieWunumqjOalvEIzMDY756ys5LiJ5a6e6aqM5qW8QjMwNzvnrKzkuInlrp7pqozmpbxCNDA2O%2BesrOS4ieWunumqjOalvEI0MDc756ys5LiJ5a6e6aqM5qW8QjQwODvnrKzkuInlrp7pqozmpbxCNDA5O%2BesrOS4ieWunumqjOalvEI0MTA756ys5LiJ5a6e6aqM5qW8QjQxMTvnrKzkuInlrp7pqozmpbxDMTAyO%2BesrOS4ieWunumqjOalvEMyMDE756ys5LiJ5a6e6aqM5qW8QzIwMzvnrKzkuInlrp7pqozmpbxDMjA2O%2BesrOS4ieWunumqjOalvEMyMTM756ys5LiJ5a6e6aqM5qW8QzIxNTvnrKzkuInlrp7pqozmpbxDMzAxO%2BesrOS4ieWunumqjOalvEMzMDM756ys5LiJ5a6e6aqM5qW8QzMwNTvnrKzkuInlrp7pqozmpbxDMzA2O%2BesrOS4ieWunumqjOalvEM0MDE756ys5LiJ5a6e6aqM5qW8QzQwNDvnrKzkuInlrp7pqozmpbxDNDA1O%2BesrOS4ieWunumqjOalvEQzMDk756ys5LiJ5a6e6aqM5qW8RDMxMDvnrKzkuInlrp7pqozmpbxENDA1O%2BesrOS4ieWunumqjOalvEQ0MDk756ys5LiJ5a6e6aqM5qW8RDQxMDvkuLvmpbw1MDEo5aSaKTvkuLvmpbw1MDgo5aSaKTvkuLvmpbw1MTIo5aSaKTvkuLvmpbw1MTUo5aSaKTvkuLvmpbw1MTYo5aSaKTvkuLvmpbw1MTco5aSaKTvkuLvmpbw1MTgo5aSaKTvkuLvmpbw1MTko5aSaKTvkuLvmpbw1MjIo5aSaKTvkuLvmpbw1MjMo5aSaKTvkuLvmpbw1MjQo5aSaKTvkuLvmpbw1MjUo5aSaKTvkuLvmpbw1MjYo5aSaKTvkuLvmpbw2MDEo5aSaKTvkuLvmpbw2MDIo5aSaKTvkuLvmpbw2MDMo5aSaKTvkuLvmpbw2MDQo5aSaKTvkuLvmpbw2MDUo5aSaKTvkuLvmpbw2MDgo5aSaKTvkuLvmpbw2MDko5aSaKTvkuLvmpbw2MTAo5aSaKTvpnJ7lsbHlm77kuabppoYxMDE76Zye5bGx5Zu%2B5Lmm6aaGMTAyO%2BmcnuWxseWbvuS5pummhjEwNDvkuLvmpbw2MTEo5aSaKTvkuLvmpbw2MTIo5aSaKTvkuLvmpbw2MTMo5aSaKTvkuLvmpbw2MTYo5aSaKTvkuLvmpbw2MTco5aSaKTvkuLvmpbw2MTgo5aSaKTvkuLvmpbw2MTko5aSaKTvkuLvmpbw2MjIo5aSaKTvkuLvmpbw2MjMo5aSaKTvkuLvmpbw2MjQo5aSaKTvkuLvmpbw2MjUo5aSaKTvkuLvmpbw2MjYo5aSaKTvkuLvmpbw3MDIo5aSaKTvkuLvmpbw3MDMo5aSaKTvkuLvmpbw3MDQo5aSaKTvkuLvmpbw3MDUo5aSaKTvkuLvmpbw3MDgo5aSaKTvkuLvmpbw3MDko5aSaKTvkuLvmpbw3MTAo5aSaKTvlhbTmlZnmpbwxMDI75YW05pWZ5qW8MTA0O%2BS4u%2BalvDcxMSjlpJopO%2BWFtOaVmealvDExMCjlpJopO%2BS4u%2BalvDcxNijlpJopO%2BS4u%2BalvDcxNyjlpJopO%2BS4u%2BalvDcxOCjlpJopO%2BWFtOaVmealvDIwMjvlhbTmlZnmpbwyMDQ75YW05pWZ5qW8MjA4O%2BS4u%2BalvDcyMijlpJopO%2BS4u%2BalvDcyMyjlpJopO%2BS4u%2BalvDcyNCjlpJopO%2BWFtOaVmealvDMwMjvlhbTmlZnmpbwzMDM75YW05pWZ5qW8MzA0O%2BWFtOaVmealvDMwNzvlhbTmlZnmpbwzMDg75YW05pWZ5qW8NDAzO%2BWFtOaVmealvDQwNTvlhbTmlZnmpbw1MDY75Li75qW8ODAxKOWkmik75Li75qW8ODA0KOWkmik75YW05rW35qW8MTAyO%2BWFtOa1t%2BalvDEwNTvlhbTmtbfmpbwxMDY75YW05rW35qW8MTA3O%2BWFtOa1t%2BalvDEwODvlhbTmtbfmpbwxMDk75YW05rW35qW8MTEyO%2BWFtOa1t%2BalvDIwMTvlhbTmtbfmpbwyMDI75YW05rW35qW8MjAzO%2BWFtOa1t%2BalvDIwOTvlhbTmtbfmpbwyMTM75YW05rW35qW8MjE1O%2BWFtOa1t%2BalvDMwMTvlhbTmtbfmpbwzMDI75YW05rW35qW8MzAzO%2BWFtOa1t%2BalvDMwNDvlhbTmtbfmpbwzMDU75YW05rW35qW8MzA3O%2BWFtOa1t%2BalvDMxMTvlhbTmtbfmpbwzMTI75YW05rW35qW8MzEzO%2BWFtOa1t%2BalvDMxNDvlhbTmtbfmpbwzMTU75YW05rW35qW8MzE2O%2BWFtOa1t%2BalvDMxNzvlhbTmtbfmpbwzMTg75YW05rW35qW8MzE5O%2BWFtOa1t%2BalvDMyMDvlhbTmtbfmpbwzMjI75YW05rW35qW8NDAxO%2BWFtOa1t%2BalvDQwMjvlhbTmtbfmpbw0MDM75YW05rW35qW8NDA0O%2BWFtOa1t%2BalvDQxMzvlhbTmtbfmpbw0MTQ75YW05rW35qW8NDE1O%2BWFtOa1t%2BalvDQxNjvlhbTmtbfmpbw0MTc75YW05rW35qW8NDIwO%2BWFtOa1t%2BalvDQyMTvlhbTmtbfmpbw0MjI75YW05rW35qW8NDIzO%2BWFtOa1t%2BalvDQyNTvlhbTmtbfmpbw1MDE75YW05rW35qW8NTAzO%2BWFtOa1t%2BalvDUwNjvlhbTmtbfmpbw1MDk75YW05rW35qW8NTExO%2BWFtOa1t%2BalvDUxMjvlhbTmtbfmpbw1MTM75YW05rW35qW8NTE0O%2BWFtOa1t%2BalvDUxNTvlhbTmtbfmpbw1MTY75YW05rW35qW8NTE3O%2BWFtOa1t%2BalvDUyMDvlhbTmtbfmpbw1MjE75YW05rW35qW8NTIyO%2BWFtOa1t%2BalvDUyMzvpkp%2FmtbfmpbwwMTAwMzvpkp%2FmtbfmpbwwMTAwNDvpkp%2FmtbfmpbwwMjAwNjvpkp%2FmtbfmpbwwMjAxMTvpkp%2FmtbfmpbwwMjAxMzvpkp%2FmtbfmpbwwMjAxNijlpJopO%2BmSn%2Ba1t%2BalvDAyMDE3KOWkmik76ZKf5rW35qW8MDIwMTgo5aSaKTvpkp%2FmtbfmpbwwMjAxOSjlpJopO%2BmSn%2Ba1t%2BalvDAyMDIwKOWkmik76ZKf5rW35qW8MDIwMjU76ZKf5rW35qW8MDIwMjY76ZKf5rW35qW8MDIwMjc76ZKf5rW35qW8MDIwMzAo5aSaKTvpkp%2FmtbfmpbwwMjAzMSjlpJopO%2BmSn%2Ba1t%2BalvDAzMDE2O%2BmSn%2Ba1t%2BalvDAzMDE5KOWkmik76ZKf5rW35qW8MDMwMjAo5aSaKTvpkp%2FmtbfmpbwwMzAyMSjlpJopO%2BmSn%2Ba1t%2BalvDAzMDIyKOWkmik76ZKf5rW35qW8MDMwMjMo5aSaKTvpkp%2FmtbfmpbwwMzAyNSjlpJopO%2BmSn%2Ba1t%2BalvDAzMDI2KOWkmik76ZKf5rW35qW8MDMwMjco5aSaKTvpkp%2FmtbfmpbwwMzAyOCjlpJopO%2BmSn%2Ba1t%2BalvDAzMDI5KOWkmik76ZKf5rW35qW8MDMwMzAo5aSaKTvpkp%2FmtbfmpbwwMzAzNSjlpJopO%2BmSn%2Ba1t%2BalvDAzMDM2KOWkmik76ZKf5rW35qW8MDMwMzco5aSaKTvpkp%2FmtbfmpbwwNDAwMjvpkp%2FmtbfmpbwwNDAwNDvpkp%2FmtbfmpbwwNDAxNzvpkp%2FmtbfmpbwwNDAxOTvpkp%2FmtbfmpbwwNDAyMSjlpJopO%2BmSn%2Ba1t%2BalvDA0MDIyKOWkmik76ZKf5rW35qW8MDQwMjMo5aSaKTvpkp%2FmtbfmpbwwNDAyNCjlpJopO%2BmSn%2Ba1t%2BalvDA0MDI1KOWkmik76ZKf5rW35qW8MDQwMjco5aSaKTvpkp%2FmtbfmpbwwNDAyOCjlpJopO%2BmSn%2Ba1t%2BalvDA0MDI5KOWkmik76ZKf5rW35qW8MDQwMzAo5aSaKTvpkp%2FmtbfmpbwwNDAzMSjlpJopO%2BmSn%2Ba1t%2BalvDA0MDMyKOWkmik76ZKf5rW35qW8MDQwMzco5aSaKTvpkp%2FmtbfmpbwwNDAzOCjlpJopO%2BmSn%2Ba1t%2BalvDA0MDM5KOWkmik76ZKf5rW35qW8MDUwMDI76ZKf5rW35qW8MDUwMDQ76ZKf5rW35qW8MDUwMDU76ZKf5rW35qW8MDUwMDc76ZKf5rW35qW8MDUwMDg76ZKf5rW35qW8MDUwMTA76ZKf5rW35qW8MDUwMTQ76ZKf5rW35qW8MDUwMTko5aSaKTvpkp%2FmtbfmpbwwNTAyMCjlpJopO%2BmSn%2Ba1t%2BalvDA1MDIxKOWkmik76ZKf5rW35qW8MDUwMjIo5aSaKTvpkp%2FmtbfmpbwwNTAyMyjlpJopO%2BmSn%2Ba1t%2BalvDA1MDI1KOWkmik76ZKf5rW35qW8MDUwMjYo5aSaKTvpkp%2FmtbfmpbwwNTAyNyjlpJopO%2BmSn%2Ba1t%2BalvDA1MDI4KOWkmik76ZKf5rW35qW8MDUwMjko5aSaKTvpkp%2FmtbfmpbwwNTAzMCjlpJopO%2BmSn%2Ba1t%2BalvDA1MDMxKOWkmik76ZKf5rW35qW8MDUwMzIo5aSaKTvpkp%2FmtbfmpbwwNTAzMyjlpJopO%2BmSn%2Ba1t%2BalvDA2MDAyO%2BmSn%2Ba1t%2BalvDA2MDA0O%2BmSn%2Ba1t%2BalvDA2MDA1O%2BmSn%2Ba1t%2BalvDA2MDA3O%2BmSn%2Ba1t%2BalvDA2MDA4O%2BmSn%2Ba1t%2BalvDA2MDEwO%2BmSn%2Ba1t%2BalvDA2MDEzO%2BmSn%2Ba1t%2BalvDA2MDE5KOWkmik76ZKf5rW35qW8MDYwMjAo5aSaKTvpkp%2FmtbfmpbwwNjAyMSjlpJopO%2BmSn%2Ba1t%2BalvDA2MDIyKOWkmik76ZKf5rW35qW8MDYwMjMo5aSaKTvpkp%2FmtbfmpbwwNjAyNSjlpJopO%2BmSn%2Ba1t%2BalvDA2MDI2KOWkmik76ZKf5rW35qW8MDYwMjco5aSaKTvpkp%2FmtbfmpbwwNjAyOCjlpJopO%2BmSn%2Ba1t%2BalvDA2MDI5KOWkmik76ZKf5rW35qW8MDYwMzAo5aSaKTvpkp%2FmtbfmpbwwNjAzMSjlpJopO%2BmSn%2Ba1t%2BalvDA2MDMyKOWkmik76ZKf5rW35qW8MDYwMzMo5aSaKTvnvo7mnK%2FmpbwxMDM7576O5pyv5qW8MTA0O%2Be%2Bjuacr%2BalvDEwNTvnvo7mnK%2FmpbwzMDI7576O5pyv5qW8MzAzO%2Be%2Bjuacr%2BalvDMwNDvnvo7mnK%2FmpbwzMDU7576O5pyv5qW8MzA2O%2Be%2Bjuacr%2BalvDMwNzvnvo7mnK%2FmpbwzMDg7576O5pyv5qW8MzA5O%2Be%2Bjuacr%2BalvDQwMjvnvo7mnK%2Fmpbw0MDM7576O5pyv5qW8NDA0O%2Be%2Bjuacr%2BalvDQwNTvnvo7mnK%2Fmpbw0MDY7576O5pyv5qW8NDA3O%2Be%2Bjuacr%2BalvDQwODvnvo7mnK%2Fmpbw0MDk7576O5pyv5qW8NDEwO%2Be%2Bjuacr%2BalvDUwMTvnvo7mnK%2Fmpbw1MDI7576O5pyv5qW8NTAzO%2Be%2Bjuacr%2BalvDUwNDvnvo7mnK%2Fmpbw1MDU7576O5pyv5qW8NTA2O%2Be%2Bjuacr%2BalvDYwMTvnvo7mnK%2Fmpbw2MDI7576O5pyv5qW8NjAzO%2Be%2Bjuacr%2BalvDYwNDvnvo7mnK%2Fmpbw2MDU7576O5pyv5qW8NjA2O%2BiInui5iOalvDEwMjvoiJ7ouYjmpbwxMDM76Iie6LmI5qW8MTA0O%2BesrOS6jOaVmeWtpualvEIxMjAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjEyMDIo5aSaKTvoiJ7ouYjmpbwyMDM76Iie6LmI5qW8MjA0O%2BiInui5iOalvDIwNTvoiJ7ouYjmpbwyMDY76Iie6LmI5qW8MjA3O%2BiInui5iOalvDIwODvnrKzkuozmlZnlrabmpbxCMTMwMSjlpJopO%2BiInui5iOalvDMwMjvnrKzkuozmlZnlrabmpbxCMTMwMijlpJopO%2BiInui5iOalvDMwMzvoiJ7ouYjmpbwzMDQ76Iie6LmI5qW8MzA1O%2BiInui5iOalvDMwNjvoiJ7ouYjmpbwzMDc76Iie6LmI5qW8MzA4O%2BiInui5iOalvDMwOTvnrKzkuozmlZnlrabmpbxCMTQwMSjlpJopO%2BiInui5iOalvDQwMjvnrKzkuozmlZnlrabmpbxCMTQwMijlpJopO%2BiInui5iOalvDQwMzvoiJ7ouYjmpbw0MDQ76Iie6LmI5qW8NDA1O%2BiInui5iOalvDQwNjvoiJ7ouYjmpbw0MDc76Iie6LmI5qW8NDA4O%2BiInui5iOalvDQwOTvnrKzkuozmlZnlrabmpbxCMTUwMSjlpJopO%2BesrOS6jOaVmeWtpualvEIxNTAyKOWkmik76Z%2Bz5LmQ5qW8MTAxO%2BesrOS6jOaVmeWtpualvEIyMTAxKOWkmik76Z%2Bz5LmQ5qW8MTAyO%2BesrOS6jOaVmeWtpualvEIyMTAyKOWkmik76Z%2Bz5LmQ5qW8MTAzO%2BesrOS6jOaVmeWtpualvEIyMjAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjIyMDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMjIwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIyMzAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjIzMDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMjMwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIyNDAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjI0MDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMjQwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIyNTAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjI1MDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMjUwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIzMTAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjMxMDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMzEwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIzMTA0KOWkmik756ys5LqM5pWZ5a2m5qW8QjMyMDEo5aSaKTvnrKzkuozmlZnlrabmpbxCMzIwMijlpJopO%2BesrOS6jOaVmeWtpualvEIzMjAzKOWkmik756ys5LqM5pWZ5a2m5qW8QjMyMDQo5aSaKTvnrKzkuozmlZnlrabmpbxCMzMwMSjlpJopO%2BesrOS6jOaVmeWtpualvEIzMzAyKOWkmik756ys5LqM5pWZ5a2m5qW8QjMzMDMo5aSaKTvnrKzkuozmlZnlrabmpbxCMzMwNCjlpJopO%2BesrOS6jOaVmeWtpualvEIzNDAxKOWkmik756ys5LqM5pWZ5a2m5qW8QjM0MDIo5aSaKTvnrKzkuozmlZnlrabmpbxCMzQwMyjlpJopO%2BesrOS6jOaVmeWtpualvEIzNDA0KOWkmik75Zu%2B5Lmm6aaGNjEwKDHlrqQpO%2BWbvuS5pummhjYxMCgy5a6kKTvpnJ7lsbFC5qW8MjAyKOWkmik76Zye5bGxQualvDIwMyjlpJopO%2BmcnuWxsULmpbwzMDIo5aSaKTvpnJ7lsbFC5qW8MzAzKOWkmik76Zye5bGxQualvDQwMijlpJopO%2BmcnuWxsULmpbw0MDMo5aSaKTvnrKzkuozmlZnlrabmpbxCNTIwMSjlpJopO%2BesrOS6jOaVmeWtpualvEI1MjAyKOWkmik756ys5LqM5pWZ5a2m5qW8QjUyMDMo5aSaKTvnrKzkuozmlZnlrabmpbxCNTMwMSjlpJopO%2BesrOS6jOaVmeWtpualvEI1MzAyKOWkmik756ys5LqM5pWZ5a2m5qW8QjUzMDMo5aSaKTvnrKzkuozmlZnlrabmpbxCNTQwMSjlpJopO%2BesrOS6jOaVmeWtpualvEI1NDAyKOWkmik756ys5LqM5pWZ5a2m5qW8QjU0MDMo5aSaKTvmsLTkuqfnp5HmioDmpbw1MjE75rC05Lqn56eR5oqA5qW8NTIyO%2BawtOS6p%2BenkeaKgOalvDUyMzvlhbTlhpzmpbwxMDM75YW05Yac5qW8MTA2O%2BWFtOWGnOalvDEwOSjlpJopO%2BWFtOWGnOalvDExMzvlhbTlhpzmpbwxMTU75YW05Yac5qW8MjAxO%2BWFtOWGnOalvDIwMjvlhbTlhpzmpbwyMDM75YW05Yac5qW8MjA4O%2BWFtOWGnOalvDIxMTvlhbTlhpzmpbwyMTI75YW05Yac5qW8MjE0O%2BWFtOWGnOalvDIxNTvlhbTlhpzmpbwyMTY75YW05Yac5qW8MzAyO%2BWFtOWGnOalvDMwNjvlhbTlhpzmpbwzMTA75YW05Yac5qW8MzExO%2BWFtOWGnOalvDMxMjvlhbTlhpzmpbwzMTQ75YW05Yac5qW8MzE4O%2BWFtOWGnOalvDQwMTvlhbTlhpzmpbw0MDM75YW05Yac5qW8NDA3O%2BWFtOWGnOalvDQxMTvlhbTlhpzmpbw0MTM75YW05Yac5qW8NDE3O%2BWFtOWGnOalvDQxODvlhbTlhpzmpbw0MTk75YW05Yac5qW8NTAyO%2BWFtOWGnOalvDUwNDvlhbTlhpzmpbw1MDY75YW05Yac5qW8NTA5O%2BWFtOWGnOalvDYwMzvlhbTlhpzmpbw2MDY756eR5oqA5qW8MTAxO%2BenkeaKgOalvDEwMjvnp5HmioDmpbwxMDM756eR5oqA5qW8MTA1O%2BenkeaKgOalvDEwNjvnp5HmioDmpbwxMDc756eR5oqA5qW8MTA4O%2BenkeaKgOalvDEwOTvnp5HmioDmpbwxMTA756eR5oqA5qW8MTExO%2BenkeaKgOalvDExMjvnp5HmioDmpbwxMTY756eR5oqA5qW8MTE3O%2BenkeaKgOalvDExODvnp5HmioDmpbwxMTk756eR5oqA5qW8MjAxO%2BenkeaKgOalvDIwMjvnp5HmioDmpbwyMDM756eR5oqA5qW8MjE0O%2BenkeaKgOalvDIxNTvnp5HmioDmpbwyMTY756eR5oqA5qW8MjE3O%2BenkeaKgOalvDIxODvnp5HmioDmpbwyMjA756eR5oqA5qW8MjIzO%2BenkeaKgOalvDIyNDvnp5HmioDmpbwyMjU756eR5oqA5qW8MjI2O%2BenkeaKgOalvDIyNzvnp5HmioDmpbwyMjg756eR5oqA5qW8MjI5O%2BenkeaKgOalvDIzMDvnp5HmioDmpbwyMzE756eR5oqA5qW8MjMyO%2BenkeaKgOalvDIzMzvnp5HmioDmpbwyMzQ756eR5oqA5qW8MjM1O%2BenkeaKgOalvDIzNjvnp5HmioDmpbwzMDE756eR5oqA5qW8MzAyO%2BenkeaKgOalvDMwMzvnp5HmioDmpbwzMDQ756eR5oqA5qW8MzEyO%2BenkeaKgOalvDMxMzvnp5HmioDmpbwzMTQ756eR5oqA5qW8MzE1O%2BenkeaKgOalvDMxNzvnp5HmioDmpbwzMTk756eR5oqA5qW8MzIwO%2BenkeaKgOalvDMyMjvnp5HmioDmpbwzMjM756eR5oqA5qW8MzI0O%2BenkeaKgOalvDMyNTvnp5HmioDmpbwzMjg756eR5oqA5qW8MzI5O%2BenkeaKgOalvDMzMDvnp5HmioDmpbwzMzI756eR5oqA5qW8MzMzO%2BenkeaKgOalvDMzNDvnp5HmioDmpbw0MDE756eR5oqA5qW8NDAzO%2BenkeaKgOalvDQxMDvnp5HmioDmpbw0MTI756eR5oqA5qW8NDEzO%2BenkeaKgOalvDQxNDvnp5HmioDmpbw0MTU756eR5oqA5qW8NDIwO%2BenkeaKgOalvDQyMTvnp5HmioDmpbw0MjI756eR5oqA5qW8NDIzO%2BenkeaKgOalvDQyNDvnp5HmioDmpbw0MjU756eR5oqA5qW8NDI3O%2BenkeaKgOalvDUwMTvnp5HmioDmpbw1MDI756eR5oqA5qW8NTAzO%2BenkeaKgOalvDUwNDvnp5HmioDmpbw1MDU756eR5oqA5qW8NTA2O%2BenkeaKgOalvDUwODvnp5HmioDmpbw1MDk756eR5oqA5qW8NTEwO%2BenkeaKgOalvDUxMTvnp5HmioDmpbw1MTI756eR5oqA5qW8NTEzO%2BenkeaKgOalvDUxNDvnp5HmioDmpbw1MTU756eR5oqA5qW8NTE2O%2BenkeaKgOalvDUxOTvnp5HmioDmpbw2MDE756eR5oqA5qW8NjAyO%2BenkeaKgOalvDYwNDvnp5HmioDmpbw2MDU756eR5oqA5qW8NjA2O%2BenkeaKgOalvDYwOTvnp5HmioDmpbw2MTA756eR5oqA5qW8NjEyO%2BenkeaKgOalvDYxMzvnp5HmioDmpbw2MTQ756eR5oqA5qW8NjE1O%2BenkeaKgOalvDYxNjvnp5HmioDmpbw2MTk75bel5Y6CMTAzO%2BW3peWOgjIwMTvlt6XljoIyMDQ75bel5Y6CMzAxO%2BWKqOeJqeino%2BWJluWupDvmlZnlrabmpbwxMDE75pWZ5a2m5qW8MTAyO%2BaVmeWtpualvDExNzvmlZnlrabmpbwxMTg75pWZ5a2m5qW8MTIwO%2BaVmeWtpualvDEyMTvmlZnlrabmpbwyMDE75pWZ5a2m5qW8MjAyO%2BaVmeWtpualvDIxODvmlZnlrabmpbwyMTk75pWZ5a2m5qW8MzAxO%2BaVmeWtpualvDMwMjvmlZnlrabmpbwzMDc75pWZ5a2m5qW8MzEwO%2BaVmeWtpualvDMxMjvmlZnlrabmpbwzMTQ75pWZ5a2m5qW8MzE1O%2BaVmeWtpualvDMxNjvmlZnlrabmpbwzMTc75pWZ5a2m5qW8MzE4O%2BaVmeWtpualvDMxOTvmlZnlrabmpbw0MDE75pWZ5a2m5qW8NDAyO%2BaVmeWtpualvDQxMDvmlZnlrabmpbw0MTI75pWZ5a2m5qW8NDEzO%2BaVmeWtpualvDQxNDvmlZnlrabmpbw0MTU75pWZ5a2m5qW8NDE2O%2BaVmeWtpualvDQxNzvmlZnlrabmpbw1MDE75pWZ5a2m5qW8NTA0O%2BaVmeWtpualvDUwNTvlrp7pqozmpbwxMDY75a6e6aqM5qW8MTA3O%2BWunumqjOalvDEwODvlrp7pqozmpbwzMDE75a6e6aqM5qW8MzA1O%2BWunumqjOalvDMwODvlrp7pqozmpbwzMDk75a6e6aqM5qW8NDA1O%2BWunumqjOalvDQwODvlrp7pqozmpbw0MDk75a6e6aqM5qW8NjAxO%2BWunumqjOalvDYwNjvnlLXmlZnmpbwxMDE755S15pWZ5qW8MTAyO%2BeUteaVmealvDEwMzvnlLXmlZnmpbwzMDE75pWZMTA055S15py65ouW5Yqo5a6e6K6t5a6kO%2BaVmTEwNeeUtei3r%2BijhemFjeWunuiureWupDvmlZkxMDblrrbnlKjnlLXlmajlrp7orq3lrqQ75pWZMTE555S15a2Q5bel6Im65a6e6K6t5a6kO%2BaVmTIwNuS8oOaEn%2BaKgOacr%2BWunuiureWupDvmlZkzMDPpq5jpopHnlLXot6%2Flrp7orq3lrqQ75pWZMzA16ISJ5Yay5pWw5a2X55S16Lev5a6e6K6t5a6kO%2BaVmTQwM%2BS9jumikeeUtei3r%2BWunuiureWupDvmlZk0MDbnlLXot6%2FliIbmnpDln7rnoYDlrp7orq3lrqQ75pWZNDA354mp55CG5a6e6aqM5a6kICA75pWZNDA45YyW5a2m5a6e6aqM5a6kICA75pWZNDEx55S15a2QQ0FE5a6e6K6t5a6kO%2BWunjMwNOWItuWGt%2BaKgOacr%2BWunuiureWupDvlrp4zMDbnp7vliqjpgJrkv6Hlrp7orq3lrqQ75a6eMzA355S15a2Q5LiO6YCa5L%2Bh5LiT5Lia5pWZ5a6kO%2BWunjQwMUHljavmmJ%2FlnLDpnaLnq5k75a6eNDAxQuWFiee6pOmAmuS%2FoeWunuiureWupDvlrp40MDLkvpvnlLXkuI7pmLLpm7fmioDmnK%2Flrp7orq3lrqQ75a6eNDAz5b2x5YOP5oqA5pyv5a6e6K6t5a6kO%2BWunjQwNuWNleeJh%2BacuuWunuiureWupDvlrp40MDfpgJrkv6HmioDmnK%2Flrp7orq3lrqQ75a6eNTA16K6h566X5py6M%2BWupDvlrp41MDborqHnrpfmnLo05a6kO%2BWunjYwMuiuoeeul%2BacujHlrqQ75a6eNjA16K6h566X5py6MuWupDvliLblhrforr7lpIflrp7orq3lrqQ75pWZ5a2m5qW8MjIwO%2BaVmeWtpualvDIyMTvnlLXmlZnmpbwyMDE755S15pWZ5qW8MjAyO%2BeUteaVmealvDIwMzs%2BO0A8MDExMDAxOzAxMTAwNjswMTIwMjswMTIwMzswMTIwNDswMTIwNTswMTIwODswMTIwOTswMTIxMDswMTIxMTswMTIxNzswMTIxODswMTIxOTswMTIyMjswMTIyMzswMTIyNDswMTIyNTswMTIyNjswMTIyNzswMTIyODswMTIzMDswMTIzMTswMTMwMTswMTMwMjswMTMwMzswMTMwNDswMTMwNTswMTMwODswMTMwOTswMTMxMDswMTMxMDE7MDEzMTAyOzAxMzEwMzswMTMxMDQ7MDEzMTA1OzAxMzExOzAxMzEyOzAxMzEzOzAxMzE2OzAxMzE3OzAxMzE4OzAxMzE5OzAxMzIwMTswMTMyMDI7MDEzMjAzOzAxMzIyOzAxMzIzOzAxMzI0OzAxMzI1OzAxMzI2OzAxMzI3OzAxMzI4OzAxMzI5OzAxMzMwOzAxNDAxOzAxNDAyOzAxNDAzOzAxNDA0OzAxNDA1OzAxNDA4OzAxNDA5OzAxNDEwOzAxNDExOzAxNDEyOzAxNDEzOzAxNDE2OzAxNDE3OzAxNDE4OzAxNDE5OzAxNDIyOzAxNDIzOzAxNDI0OzAxNDI1OzAxNDI2OzAxNEExMDEtMTswMTRBMTAxLTI7MDE0QTEwMjswMTRBMTA0OzAxNEEyMDI7MDE0QTIwNDswMTRBMjA3OzAxNEEyMDk7MDE0QTIxMTswMTRBMjEyOzAxNEEyMTM7MDE0QjIwOTswMTRCMzA2OzAxNEIzMDc7MDE0QjQwNjswMTRCNDA3OzAxNEI0MDg7MDE0QjQwOTswMTRCNDEwOzAxNEI0MTE7MDE0QzEwMjswMTRDMjAxOzAxNEMyMDM7MDE0QzIwNjswMTRDMjEzOzAxNEMyMTU7MDE0QzMwMTswMTRDMzAzOzAxNEMzMDU7MDE0QzMwNjswMTRDNDAxOzAxNEM0MDQ7MDE0QzQwNTswMTREMzA5OzAxNEQzMTA7MDE0RDQwNTswMTRENDA5OzAxNEQ0MTA7MDE1MDE7MDE1MDg7MDE1MTI7MDE1MTU7MDE1MTY7MDE1MTc7MDE1MTg7MDE1MTk7MDE1MjI7MDE1MjM7MDE1MjQ7MDE1MjU7MDE1MjY7MDE2MDE7MDE2MDI7MDE2MDM7MDE2MDQ7MDE2MDU7MDE2MDg7MDE2MDk7MDE2MTA7MDE2MTAxOzAxNjEwMjswMTYxMDQ7MDE2MTE7MDE2MTI7MDE2MTM7MDE2MTY7MDE2MTc7MDE2MTg7MDE2MTk7MDE2MjI7MDE2MjM7MDE2MjQ7MDE2MjU7MDE2MjY7MDE3MDI7MDE3MDM7MDE3MDQ7MDE3MDU7MDE3MDg7MDE3MDk7MDE3MTA7MDE3MTAyOzAxNzEwNDswMTcxMTswMTcxMTA7MDE3MTY7MDE3MTc7MDE3MTg7MDE3MjAyOzAxNzIwNDswMTcyMDg7MDE3MjI7MDE3MjM7MDE3MjQ7MDE3MzAyOzAxNzMwMzswMTczMDQ7MDE3MzA3OzAxNzMwODswMTc0MDM7MDE3NDA1OzAxNzUwNjswMTgwMTswMTgwNDswMTgxMDI7MDE4MTA1OzAxODEwNjswMTgxMDc7MDE4MTA4OzAxODEwOTswMTgxMTI7MDE4MjAxOzAxODIwMjswMTgyMDM7MDE4MjA5OzAxODIxMzswMTgyMTU7MDE4MzAxOzAxODMwMjswMTgzMDM7MDE4MzA0OzAxODMwNTswMTgzMDc7MDE4MzExOzAxODMxMjswMTgzMTM7MDE4MzE0OzAxODMxNTswMTgzMTY7MDE4MzE3OzAxODMxODswMTgzMTk7MDE4MzIwOzAxODMyMjswMTg0MDE7MDE4NDAyOzAxODQwMzswMTg0MDQ7MDE4NDEzOzAxODQxNDswMTg0MTU7MDE4NDE2OzAxODQxNzswMTg0MjA7MDE4NDIxOzAxODQyMjswMTg0MjM7MDE4NDI1OzAxODUwMTswMTg1MDM7MDE4NTA2OzAxODUwOTswMTg1MTE7MDE4NTEyOzAxODUxMzswMTg1MTQ7MDE4NTE1OzAxODUxNjswMTg1MTc7MDE4NTIwOzAxODUyMTswMTg1MjI7MDE4NTIzOzAxOTAxMDAzOzAxOTAxMDA0OzAxOTAyMDA2OzAxOTAyMDExOzAxOTAyMDEzOzAxOTAyMDE2OzAxOTAyMDE3OzAxOTAyMDE4OzAxOTAyMDE5OzAxOTAyMDIwOzAxOTAyMDI1OzAxOTAyMDI2OzAxOTAyMDI3OzAxOTAyMDMwOzAxOTAyMDMxOzAxOTAzMDE2OzAxOTAzMDE5OzAxOTAzMDIwOzAxOTAzMDIxOzAxOTAzMDIyOzAxOTAzMDIzOzAxOTAzMDI1OzAxOTAzMDI2OzAxOTAzMDI3OzAxOTAzMDI4OzAxOTAzMDI5OzAxOTAzMDMwOzAxOTAzMDM1OzAxOTAzMDM2OzAxOTAzMDM3OzAxOTA0MDAyOzAxOTA0MDA0OzAxOTA0MDE3OzAxOTA0MDE5OzAxOTA0MDIxOzAxOTA0MDIyOzAxOTA0MDIzOzAxOTA0MDI0OzAxOTA0MDI1OzAxOTA0MDI3OzAxOTA0MDI4OzAxOTA0MDI5OzAxOTA0MDMwOzAxOTA0MDMxOzAxOTA0MDMyOzAxOTA0MDM3OzAxOTA0MDM4OzAxOTA0MDM5OzAxOTA1MDAyOzAxOTA1MDA0OzAxOTA1MDA1OzAxOTA1MDA3OzAxOTA1MDA4OzAxOTA1MDEwOzAxOTA1MDE0OzAxOTA1MDE5OzAxOTA1MDIwOzAxOTA1MDIxOzAxOTA1MDIyOzAxOTA1MDIzOzAxOTA1MDI1OzAxOTA1MDI2OzAxOTA1MDI3OzAxOTA1MDI4OzAxOTA1MDI5OzAxOTA1MDMwOzAxOTA1MDMxOzAxOTA1MDMyOzAxOTA1MDMzOzAxOTA2MDAyOzAxOTA2MDA0OzAxOTA2MDA1OzAxOTA2MDA3OzAxOTA2MDA4OzAxOTA2MDEwOzAxOTA2MDEzOzAxOTA2MDE5OzAxOTA2MDIwOzAxOTA2MDIxOzAxOTA2MDIyOzAxOTA2MDIzOzAxOTA2MDI1OzAxOTA2MDI2OzAxOTA2MDI3OzAxOTA2MDI4OzAxOTA2MDI5OzAxOTA2MDMwOzAxOTA2MDMxOzAxOTA2MDMyOzAxOTA2MDMzOzAyMDEwMzswMjAxMDQ7MDIwMTA1OzAyMDMwMjswMjAzMDM7MDIwMzA0OzAyMDMwNTswMjAzMDY7MDIwMzA3OzAyMDMwODswMjAzMDk7MDIwNDAyOzAyMDQwMzswMjA0MDQ7MDIwNDA1OzAyMDQwNjswMjA0MDc7MDIwNDA4OzAyMDQwOTswMjA0MTA7MDIwNTAxOzAyMDUwMjswMjA1MDM7MDIwNTA0OzAyMDUwNTswMjA1MDY7MDIwNjAxOzAyMDYwMjswMjA2MDM7MDIwNjA0OzAyMDYwNTswMjA2MDY7MDIxMTAyOzAyMTEwMzswMjExMDQ7MDIxMjAxOzAyMTIwMjswMjEyMDM7MDIxMjA0OzAyMTIwNTswMjEyMDY7MDIxMjA3OzAyMTIwODswMjEzMDE7MDIxMzAyOzAyMTMwMuaVmeWtpualvDswMjEzMDM7MDIxMzA0OzAyMTMwNTswMjEzMDY7MDIxMzA3OzAyMTMwODswMjEzMDk7MDIxNDAxOzAyMTQwMjswMjE0MDLmlZnlrabmpbw7MDIxNDAzOzAyMTQwNDswMjE0MDU7MDIxNDA2OzAyMTQwNzswMjE0MDg7MDIxNDA5OzAyMTUwMTswMjE1MDI7MDIyMTAxOzAyMjEwMeaVmeWtpualvDswMjIxMDI7MDIyMTAy5pWZ5a2m5qW8OzAyMjEwMzswMjIyMDE7MDIyMjAyOzAyMjIwMzswMjIzMDE7MDIyMzAyOzAyMjMwMzswMjI0MDE7MDIyNDAyOzAyMjQwMzswMjI1MDE7MDIyNTAyOzAyMjUwMzswMjMxMDE7MDIzMTAyOzAyMzEwMzswMjMxMDQ7MDIzMjAxOzAyMzIwMjswMjMyMDM7MDIzMjA0OzAyMzMwMTswMjMzMDI7MDIzMzAzOzAyMzMwNDswMjM0MDE7MDIzNDAyOzAyMzQwMzswMjM0MDQ7MDIzNjEwVDE7MDIzNjEwVDI7MDI0MjAyOzAyNDIwMzswMjQzMDI7MDI0MzAzOzAyNDQwMjswMjQ0MDM7MDI1MjAxOzAyNTIwMjswMjUyMDM7MDI1MzAxOzAyNTMwMjswMjUzMDM7MDI1NDAxOzAyNTQwMjswMjU0MDM7MDI4NTIxOzAyODUyMjswMjg1MjM7MDQxMDM7MDQxMDY7MDQxMDk7MDQxMTM7MDQxMTU7MDQyMDE7MDQyMDI7MDQyMDM7MDQyMDg7MDQyMTE7MDQyMTI7MDQyMTQ7MDQyMTU7MDQyMTY7MDQzMDI7MDQzMDY7MDQzMTA7MDQzMTE7MDQzMTI7MDQzMTQ7MDQzMTg7MDQ0MDE7MDQ0MDM7MDQ0MDc7MDQ0MTE7MDQ0MTM7MDQ0MTc7MDQ0MTg7MDQ0MTk7MDQ1MDI7MDQ1MDQ7MDQ1MDY7MDQ1MDk7MDQ2MDM7MDQ2MDY7MDUxMDE7MDUxMDI7MDUxMDM7MDUxMDU7MDUxMDY7MDUxMDc7MDUxMDg7MDUxMDk7MDUxMTA7MDUxMTE7MDUxMTI7MDUxMTY7MDUxMTc7MDUxMTg7MDUxMTk7MDUyMDE7MDUyMDI7MDUyMDM7MDUyMTQ7MDUyMTU7MDUyMTY7MDUyMTc7MDUyMTg7MDUyMjA7MDUyMjM7MDUyMjQ7MDUyMjU7MDUyMjY7MDUyMjc7MDUyMjg7MDUyMjk7MDUyMzA7MDUyMzE7MDUyMzI7MDUyMzM7MDUyMzQ7MDUyMzU7MDUyMzY7MDUzMDE7MDUzMDI7MDUzMDM7MDUzMDQ7MDUzMTI7MDUzMTM7MDUzMTQ7MDUzMTU7MDUzMTc7MDUzMTk7MDUzMjA7MDUzMjI7MDUzMjM7MDUzMjQ7MDUzMjU7MDUzMjg7MDUzMjk7MDUzMzA7MDUzMzI7MDUzMzM7MDUzMzQ7MDU0MDE7MDU0MDM7MDU0MTA7MDU0MTI7MDU0MTM7MDU0MTQ7MDU0MTU7MDU0MjA7MDU0MjE7MDU0MjI7MDU0MjM7MDU0MjQ7MDU0MjU7MDU0Mjc7MDU1MDE7MDU1MDI7MDU1MDM7MDU1MDQ7MDU1MDU7MDU1MDY7MDU1MDg7MDU1MDk7MDU1MTA7MDU1MTE7MDU1MTI7MDU1MTM7MDU1MTQ7MDU1MTU7MDU1MTY7MDU1MTk7MDU2MDE7MDU2MDI7MDU2MDQ7MDU2MDU7MDU2MDY7MDU2MDk7MDU2MTA7MDU2MTI7MDU2MTM7MDU2MTQ7MDU2MTU7MDU2MTY7MDU2MTk7MDgxMDM7MDgyMDE7MDgyMDQ7MDgzMDE7MTEwMDE7TTExMDE7TTExMDI7TTExMTc7TTExMTg7TTExMjA7TTExMjE7TTEyMDE7TTEyMDI7TTEyMTg7TTEyMTk7TTEzMDE7TTEzMDI7TTEzMDc7TTEzMTA7TTEzMTI7TTEzMTQ7TTEzMTU7TTEzMTY7TTEzMTc7TTEzMTg7TTEzMTk7TTE0MDE7TTE0MDI7TTE0MTA7TTE0MTI7TTE0MTM7TTE0MTQ7TTE0MTU7TTE0MTY7TTE0MTc7TTE1MDE7TTE1MDQ7TTE1MDU7TTIxMDY7TTIxMDc7TTIxMDg7TTIzMDE7TTIzMDU7TTIzMDg7TTIzMDk7TTI0MDU7TTI0MDg7TTI0MDk7TTI2MDE7TTI2MDY7TTMxMDE7TTMxMDI7TTMxMDM7TTMzMDE7UzExMDQ7UzExMDU7UzExMDY7UzExMTk7UzEyMDY7UzEzMDM7UzEzMDU7UzE0MDM7UzE0MDY7UzE0MDc7UzE0MDg7UzE0NDE7UzIzMDQ7UzIzMDY7UzIzMDc7UzI0MDFBO1MyNDAxQjtTMjQwMjtTMjQwMztTMjQwNjtTMjQwNztTMjUwMTtTMjUwNjtTMjYwMjtTMjYwNTtTMzAwMTtZMTIyMDtZMTIyMTtZMzIwMTtZMzIwMjtZMzIwMzs%2BPjs%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4bjt4bjs%2BPjs%2BO3Q8aTwxMD47QDwyMDA3LTIwMDg7MjAwOC0yMDA5OzIwMDktMjAxMDsyMDEwLTIwMTE7MjAxMS0yMDEyOzIwMTItMjAxMzsyMDEzLTIwMTQ7MjAxNC0yMDE1OzIwMTUtMjAxNjsyMDE2LTIwMTc7PjtAPDIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzs%2BPjs%2BOzs%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDQ5PjtpPDQ5PjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BO2k8OD47aTw5PjtpPDEwPjtpPDExPjtpPDEyPjtpPDEzPjtpPDE0PjtpPDE1PjtpPDE2PjtpPDE3PjtpPDE4PjtpPDE5PjtpPDIwPjtpPDIxPjtpPDIyPjtpPDIzPjtpPDI0PjtpPDI1PjtpPDI2PjtpPDI3PjtpPDI4PjtpPDI5PjtpPDMwPjtpPDMxPjtpPDMyPjtpPDMzPjtpPDM0PjtpPDM1PjtpPDM2PjtpPDM3PjtpPDM4PjtpPDM5PjtpPDQwPjtpPDQxPjtpPDQyPjtpPDQzPjtpPDQ0PjtpPDQ1PjtpPDQ2PjtpPDQ3PjtpPDQ4PjtpPDQ5Pjs%2BO2w8dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE0Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8xOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDEsMuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDMsNOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE2Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8xOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwzLDToioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8xOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw1LDboioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8xOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw3LDjoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8yOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8yOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwzLDToioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8yOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw1LDboioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDcsOOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8ODs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDcsOOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8NTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysOSwxMOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8ODs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDksMTDoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfMzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8zOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzM7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDMsNOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE0Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8zOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwzLDToioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDg7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ8zOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw1LDboioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzM7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDcsOOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8Njs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Njs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlj4w7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDEsMuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85Y2VOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDw1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWNlTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWNlTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8ODs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8ODs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlj4w7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDEsMuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85Y2VOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWPjDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE2Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWPjDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE0Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWPjDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8Mzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Mzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzljZU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDEsMuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85Y2VOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDw0Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw0Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWPjDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDExOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWNlTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ80Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWPjDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDMsNOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTA7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDUsNuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzQ7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDcsOOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BO2k8ND47aTw1PjtpPDY%2BO2k8Nz47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8NTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDw4Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxNDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMSwy6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMyw06IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysNSw26IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDw4Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysNyw46IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ81Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw3LDjoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDksMTDoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDg7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ81Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKw5LDEw6IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDEsMuiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85Y%2BMOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuIror747Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47aTw3Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwxOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDw5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzmmJ%2FmnJ82Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDznrKwxLDLoioI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85pif5pyfNjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w856ysMyw06IqCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlj4w7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4iuivvjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47aTw2PjtpPDc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOaYn%2BacnzY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOesrDMsNOiKgjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8Jm5ic3BcOzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiK6K%2B%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwmbmJzcFw7Oz4%2BOz47Oz47Pj47Pj47Pj47dDxwPHA8bDxRdWVyeTtRdWVyeVBhcmFtcztkdFJlY29yZHM7PjtsPFNFTEVDVCBhLnFzeiDlvIDlp4vlkagsIGEuanN6IOe7k%2Badn%2BWRqCwgJ%2BaYn%2Bacnyd8fGEueHFqIOaYn%2Bacn%2BWHoCwgYi5qdHNqIOWFt%2BS9k%2BaXtumXtCwgYS5kc3og5Y2V5Y%2BM5ZGoLCBhLnBrIOS9v%2BeUqOaWueW8jywnJyBqc3htLCcnIGJqaHogRlJPTSBqeGNkdmlldyBhLCBrY3NqZHpiIGIsanhjZHh4YiBjICBXSEVSRSBhLmpzYmg9OnBhcmFtc3RyMSBBTkQgYS54bj0nMjAxNC0yMDE1JyBBTkQgYS54cT0nMicgQU5EIGIueG49JzIwMTQtMjAxNScgQU5EIGIueHE9JzInIEFORCBhLnNqZD1iLnNqZCAgQU5EIGEuanNiaD1jLmpzYmggYW5kIGMuS1lGPSdUJyBPUkRFUiBCWSBhLnhxaixhLnNqZDtAPDAxMjAyOz47YjxBQUVBQUFELy8vLy9BUUFBQUFBQUFBQU1BZ0FBQUZGVGVYTjBaVzB1UkdGMFlTd2dWbVZ5YzJsdmJqMHhMakF1TlRBd01DNHdMQ0JEZFd4MGRYSmxQVzVsZFhSeVlXd3NJRkIxWW14cFkwdGxlVlJ2YTJWdVBXSTNOMkUxWXpVMk1Ua3pOR1V3T0RrRkFRQUFBQlZUZVhOMFpXMHVSR0YwWVM1RVlYUmhWR0ZpYkdVQ0FBQUFDVmh0YkZOamFHVnRZUXRZYld4RWFXWm1SM0poYlFFQkFnQUFBQVlEQUFBQTVBbzhQM2h0YkNCMlpYSnphVzl1UFNJeExqQWlJR1Z1WTI5a2FXNW5QU0oxZEdZdE1UWWlQejROQ2p4NGN6cHpZMmhsYldFZ2FXUTlJazVsZDBSaGRHRlRaWFFpSUhodGJHNXpQU0lpSUhodGJHNXpPbmh6UFNKb2RIUndPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1EQXhMMWhOVEZOamFHVnRZU0lnZUcxc2JuTTZiWE5rWVhSaFBTSjFjbTQ2YzJOb1pXMWhjeTF0YVdOeWIzTnZablF0WTI5dE9uaHRiQzF0YzJSaGRHRWlQZzBLSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKVVlXSnNaU0krRFFvZ0lDQWdQSGh6T21OdmJYQnNaWGhVZVhCbFBnMEtJQ0FnSUNBZ1BIaHpPbk5sY1hWbGJtTmxQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0xsdklEbHA0dmxrYWdpSUhSNWNHVTlJbmh6T21SbFkybHRZV3dpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0xudTVQbW5aL2xrYWdpSUhSNWNHVTlJbmh6T21SbFkybHRZV3dpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0xtbUovbW5KL2xoNkFpSUhSNWNHVTlJbmh6T25OMGNtbHVaeUlnYlhOa1lYUmhPblJoY21kbGRFNWhiV1Z6Y0dGalpUMGlJaUJ0YVc1UFkyTjFjbk05SWpBaUlDOCtEUW9nSUNBZ0lDQWdJRHg0Y3pwbGJHVnRaVzUwSUc1aGJXVTlJdVdGdCtTOWsrYVh0dW1YdENJZ2RIbHdaVDBpZUhNNmMzUnlhVzVuSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJQ0FnUEhoek9tVnNaVzFsYm5RZ2JtRnRaVDBpNVkyVjVZK001WkdvSWlCMGVYQmxQU0o0Y3pwemRISnBibWNpSUcxelpHRjBZVHAwWVhKblpYUk9ZVzFsYzNCaFkyVTlJaUlnYldsdVQyTmpkWEp6UFNJd0lpQXZQZzBLSUNBZ0lDQWdJQ0E4ZUhNNlpXeGxiV1Z1ZENCdVlXMWxQU0xrdmIvbmxLam1scm5sdkk4aUlIUjVjR1U5SW5oek9uTjBjbWx1WnlJZ2JYTmtZWFJoT25SaGNtZGxkRTVoYldWemNHRmpaVDBpSWlCdGFXNVBZMk4xY25NOUlqQWlJQzgrRFFvZ0lDQWdJQ0FnSUR4NGN6cGxiR1Z0Wlc1MElHNWhiV1U5SWtwVFdFMGlJSFI1Y0dVOUluaHpPbk4wY21sdVp5SWdiWE5rWVhSaE9uUmhjbWRsZEU1aGJXVnpjR0ZqWlQwaUlpQnRhVzVQWTJOMWNuTTlJakFpSUM4K0RRb2dJQ0FnSUNBZ0lEeDRjenBsYkdWdFpXNTBJRzVoYldVOUlrSktTRm9pSUhSNWNHVTlJbmh6T25OMGNtbHVaeUlnYlhOa1lYUmhPblJoY21kbGRFNWhiV1Z6Y0dGalpUMGlJaUJ0YVc1UFkyTjFjbk05SWpBaUlDOCtEUW9nSUNBZ0lDQWdJRHg0Y3pwbGJHVnRaVzUwSUc1aGJXVTlJbEpPSWlCMGVYQmxQU0o0Y3pwa1pXTnBiV0ZzSWlCdGMyUmhkR0U2ZEdGeVoyVjBUbUZ0WlhOd1lXTmxQU0lpSUcxcGJrOWpZM1Z5Y3owaU1DSWdMejROQ2lBZ0lDQWdJRHd2ZUhNNmMyVnhkV1Z1WTJVK0RRb2dJQ0FnUEM5NGN6cGpiMjF3YkdWNFZIbHdaVDROQ2lBZ1BDOTRjenBsYkdWdFpXNTBQZzBLSUNBOGVITTZaV3hsYldWdWRDQnVZVzFsUFNKT1pYZEVZWFJoVTJWMElpQnRjMlJoZEdFNlNYTkVZWFJoVTJWMFBTSjBjblZsSWlCdGMyUmhkR0U2VEc5allXeGxQU0o2YUMxRFRpSStEUW9nSUNBZ1BIaHpPbU52YlhCc1pYaFVlWEJsUGcwS0lDQWdJQ0FnUEhoek9tTm9iMmxqWlNCdFlYaFBZMk4xY25NOUluVnVZbTkxYm1SbFpDSWdMejROQ2lBZ0lDQThMM2h6T21OdmJYQnNaWGhVZVhCbFBnMEtJQ0E4TDNoek9tVnNaVzFsYm5RK0RRbzhMM2h6T25OamFHVnRZVDRHQkFBQUFOVnVQR1JwWm1abmNqcGthV1ptWjNKaGJTQjRiV3h1Y3pwdGMyUmhkR0U5SW5WeWJqcHpZMmhsYldGekxXMXBZM0p2YzI5bWRDMWpiMjA2ZUcxc0xXMXpaR0YwWVNJZ2VHMXNibk02WkdsbVptZHlQU0oxY200NmMyTm9aVzFoY3kxdGFXTnliM052Wm5RdFkyOXRPbmh0YkMxa2FXWm1aM0poYlMxMk1TSStEUW9nSUR4T1pYZEVZWFJoVTJWMFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxNU0lnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0l3SWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK01USThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTVRROEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk1Ud3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3hMRExvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHprdmIvbmxLam1scm5sdkk4K2NHczhMK1M5ditlVXFPYVd1ZVc4ano0TkNpQWdJQ0FnSUR4U1RqNHhQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTWlJZ2JYTmtZWFJoT25KdmQwOXlaR1Z5UFNJeElqNE5DaUFnSUNBZ0lEemx2SURscDR2bGthZytNVHd2NWJ5QTVhZUw1WkdvUGcwS0lDQWdJQ0FnUE9lN2srYWRuK1dScUQ0NVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256RThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNNU3d5NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtNand2VWs0K0RRb2dJQ0FnUEM5VVlXSnNaVDROQ2lBZ0lDQThWR0ZpYkdVZ1pHbG1abWR5T21sa1BTSlVZV0pzWlRNaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU1pSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QakU4TCtXOGdPV25pK1dScUQ0TkNpQWdJQ0FnSUR6bnU1UG1uWi9sa2FnK01USThMK2U3aythZG4rV1JxRDROQ2lBZ0lDQWdJRHptbUovbW5KL2xoNkErNXBpZjVweWZNVHd2NXBpZjVweWY1WWVnUGcwS0lDQWdJQ0FnUE9XRnQrUzlrK2FYdHVtWHREN25yS3d6TERUb2lvSThMK1dGdCtTOWsrYVh0dW1YdEQ0TkNpQWdJQ0FnSUR6a3ZiL25sS2ptbHJubHZJOCtjR3M4TCtTOXYrZVVxT2FXdWVXOGp6NE5DaUFnSUNBZ0lEeFNUajR6UEM5U1RqNE5DaUFnSUNBOEwxUmhZbXhsUGcwS0lDQWdJRHhVWVdKc1pTQmthV1ptWjNJNmFXUTlJbFJoWW14bE5DSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXpJajROQ2lBZ0lDQWdJRHpsdklEbHA0dmxrYWcrTVRVOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytNVFk4TCtlN2srYWRuK1dScUQ0TkNpQWdJQ0FnSUR6bW1KL21uSi9saDZBKzVwaWY1cHlmTVR3djVwaWY1cHlmNVllZ1BnMEtJQ0FnSUNBZ1BPV0Z0K1M5aythWHR1bVh0RDduckt3ekxEVG9pb0k4TCtXRnQrUzlrK2FYdHVtWHRENE5DaUFnSUNBZ0lEemt2Yi9ubEtqbWxybmx2STgrY0dzOEwrUzl2K2VVcU9hV3VlVzhqejROQ2lBZ0lDQWdJRHhTVGo0MFBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxOU0lnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0kwSWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK056d3Y1YnlBNWFlTDVaR29QZzBLSUNBZ0lDQWdQT2U3aythZG4rV1JxRDR4TWp3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4eFBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JEVXNOdWlLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqVThMMUpPUGcwS0lDQWdJRHd2VkdGaWJHVStEUW9nSUNBZ1BGUmhZbXhsSUdScFptWm5janBwWkQwaVZHRmliR1UySWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpVaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENHhQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpFeVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256RThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNOeXc0NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtOand2VWs0K0RRb2dJQ0FnUEM5VVlXSnNaVDROQ2lBZ0lDQThWR0ZpYkdVZ1pHbG1abWR5T21sa1BTSlVZV0pzWlRjaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU5pSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QakV5UEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRTBQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuekk4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVMMi81NVNvNXBhNTVieVBQbkJyUEMva3ZiL25sS2ptbHJubHZJOCtEUW9nSUNBZ0lDQThVazQrTnp3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdJQ0E4VkdGaWJHVWdaR2xtWm1keU9tbGtQU0pVWVdKc1pUZ2lJRzF6WkdGMFlUcHliM2RQY21SbGNqMGlOeUkrRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytPVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjh5UEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRFc011aUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpnOEwxSk9QZzBLSUNBZ0lEd3ZWR0ZpYkdVK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVTVJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJamdpUGcwS0lDQWdJQ0FnUE9XOGdPV25pK1dScUQ0eFBDL2x2SURscDR2bGthZytEUW9nSUNBZ0lDQTg1N3VUNXAyZjVaR29QakV5UEMvbnU1UG1uWi9sa2FnK0RRb2dJQ0FnSUNBODVwaWY1cHlmNVllZ1B1YVluK2FjbnpJOEwrYVluK2FjbitXSG9ENE5DaUFnSUNBZ0lEemxoYmZrdlpQbWw3YnBsN1ErNTZ5c015dzA2SXFDUEMvbGhiZmt2WlBtbDdicGw3UStEUW9nSUNBZ0lDQTg1TDIvNTVTbzVwYTU1YnlQUG5CclBDL2t2Yi9ubEtqbWxybmx2STgrRFFvZ0lDQWdJQ0E4VWs0K09Ud3ZVazQrRFFvZ0lDQWdQQzlVWVdKc1pUNE5DaUFnSUNBOFZHRmliR1VnWkdsbVptZHlPbWxrUFNKVVlXSnNaVEV3SWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpraVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENHhQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpFeVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256SThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNOU3cyNklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtNVEE4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVeE1TSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXhNQ0krRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytOVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjh5UEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRjc09PaUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpFeFBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxNVElpSUcxelpHRjBZVHB5YjNkUGNtUmxjajBpTVRFaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENDRQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpFeVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256SThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNOeXc0NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtNVEk4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVeE15SWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXhNaUkrRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytOVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjh5UEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRrc01URG9pb0k4TCtXRnQrUzlrK2FYdHVtWHRENE5DaUFnSUNBZ0lEemt2Yi9ubEtqbWxybmx2STgrY0dzOEwrUzl2K2VVcU9hV3VlVzhqejROQ2lBZ0lDQWdJRHhTVGo0eE16d3ZVazQrRFFvZ0lDQWdQQzlVWVdKc1pUNE5DaUFnSUNBOFZHRmliR1VnWkdsbVptZHlPbWxrUFNKVVlXSnNaVEUwSWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpFeklqNE5DaUFnSUNBZ0lEemx2SURscDR2bGthZytPRHd2NWJ5QTVhZUw1WkdvUGcwS0lDQWdJQ0FnUE9lN2srYWRuK1dScUQ0eE1qd3Y1N3VUNXAyZjVaR29QZzBLSUNBZ0lDQWdQT2FZbithY24rV0hvRDdtbUovbW5KOHlQQy9tbUovbW5KL2xoNkErRFFvZ0lDQWdJQ0E4NVlXMzVMMlQ1cGUyNlplMFB1ZXNyRGtzTVREb2lvSThMK1dGdCtTOWsrYVh0dW1YdEQ0TkNpQWdJQ0FnSUR6a3ZiL25sS2ptbHJubHZJOCtjR3M4TCtTOXYrZVVxT2FXdWVXOGp6NE5DaUFnSUNBZ0lEeFNUajR4TkR3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdJQ0E4VkdGaWJHVWdaR2xtWm1keU9tbGtQU0pVWVdKc1pURTFJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJakUwSWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK01USThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTVRROEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk16d3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3hMRExvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHprdmIvbmxLam1scm5sdkk4K2NHczhMK1M5ditlVXFPYVd1ZVc4ano0TkNpQWdJQ0FnSUR4U1RqNHhOVHd2VWs0K0RRb2dJQ0FnUEM5VVlXSnNaVDROQ2lBZ0lDQThWR0ZpYkdVZ1pHbG1abWR5T21sa1BTSlVZV0pzWlRFMklpQnRjMlJoZEdFNmNtOTNUM0prWlhJOUlqRTFJajROQ2lBZ0lDQWdJRHpsdklEbHA0dmxrYWcrTVR3djVieUE1YWVMNVpHb1BnMEtJQ0FnSUNBZ1BPZTdrK2FkbitXUnFENDVQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuek04TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVMMi81NVNvNXBhNTVieVBQbkJyUEMva3ZiL25sS2ptbHJubHZJOCtEUW9nSUNBZ0lDQThVazQrTVRZOEwxSk9QZzBLSUNBZ0lEd3ZWR0ZpYkdVK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVXhOeUlnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0l4TmlJK0RRb2dJQ0FnSUNBODVieUE1YWVMNVpHb1BqRThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrT1R3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4elBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JETXNOT2lLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqRTNQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTVRnaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU1UY2lQZzBLSUNBZ0lDQWdQT1c4Z09XbmkrV1JxRDR4TWp3djVieUE1YWVMNVpHb1BnMEtJQ0FnSUNBZ1BPZTdrK2FkbitXUnFENHhORHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjh6UEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRNc05PaUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpFNFBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxNVGtpSUcxelpHRjBZVHB5YjNkUGNtUmxjajBpTVRnaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENDRQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpFMVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256TThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNOU3cyNklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtNVGs4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVeU1DSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXhPU0krRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytOand2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjh6UEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRjc09PaUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpJd1BDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxNakVpSUcxelpHRjBZVHB5YjNkUGNtUmxjajBpTWpBaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENDJQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpZOEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk5Ed3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3hMRExvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHpsalpYbGo0emxrYWcrNVkrTVBDL2xqWlhsajR6bGthZytEUW9nSUNBZ0lDQTg1TDIvNTVTbzVwYTU1YnlQUG5CclBDL2t2Yi9ubEtqbWxybmx2STgrRFFvZ0lDQWdJQ0E4VWs0K01qRThMMUpPUGcwS0lDQWdJRHd2VkdGaWJHVStEUW9nSUNBZ1BGUmhZbXhsSUdScFptWm5janBwWkQwaVZHRmliR1V5TWlJZ2JYTmtZWFJoT25KdmQwOXlaR1Z5UFNJeU1TSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QamM4TCtXOGdPV25pK1dScUQ0TkNpQWdJQ0FnSUR6bnU1UG1uWi9sa2FnK056d3Y1N3VUNXAyZjVaR29QZzBLSUNBZ0lDQWdQT2FZbithY24rV0hvRDdtbUovbW5KODBQQy9tbUovbW5KL2xoNkErRFFvZ0lDQWdJQ0E4NVlXMzVMMlQ1cGUyNlplMFB1ZXNyREVzTXVpS2dqd3Y1WVczNUwyVDVwZTI2WmUwUGcwS0lDQWdJQ0FnUE9XTmxlV1BqT1dScUQ3bGpaVThMK1dObGVXUGpPV1JxRDROQ2lBZ0lDQWdJRHprdmIvbmxLam1scm5sdkk4K2NHczhMK1M5ditlVXFPYVd1ZVc4ano0TkNpQWdJQ0FnSUR4U1RqNHlNand2VWs0K0RRb2dJQ0FnUEM5VVlXSnNaVDROQ2lBZ0lDQThWR0ZpYkdVZ1pHbG1abWR5T21sa1BTSlVZV0pzWlRJeklpQnRjMlJoZEdFNmNtOTNUM0prWlhJOUlqSXlJajROQ2lBZ0lDQWdJRHpsdklEbHA0dmxrYWcrTlR3djVieUE1YWVMNVpHb1BnMEtJQ0FnSUNBZ1BPZTdrK2FkbitXUnFENDFQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuelE4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVZMlY1WStNNVpHb1B1V05sVHd2NVkyVjVZK001WkdvUGcwS0lDQWdJQ0FnUE9TOXYrZVVxT2FXdWVXOGp6NXdhend2NUwyLzU1U281cGE1NWJ5UFBnMEtJQ0FnSUNBZ1BGSk9Qakl6UEM5U1RqNE5DaUFnSUNBOEwxUmhZbXhsUGcwS0lDQWdJRHhVWVdKc1pTQmthV1ptWjNJNmFXUTlJbFJoWW14bE1qUWlJRzF6WkdGMFlUcHliM2RQY21SbGNqMGlNak1pUGcwS0lDQWdJQ0FnUE9XOGdPV25pK1dScUQ0eE5Ud3Y1YnlBNWFlTDVaR29QZzBLSUNBZ0lDQWdQT2U3aythZG4rV1JxRDR4TlR3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4MFBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JERXNNdWlLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1dObGVXUGpPV1JxRDdsalpVOEwrV05sZVdQak9XUnFENE5DaUFnSUNBZ0lEemt2Yi9ubEtqbWxybmx2STgrY0dzOEwrUzl2K2VVcU9hV3VlVzhqejROQ2lBZ0lDQWdJRHhTVGo0eU5Ed3ZVazQrRFFvZ0lDQWdQQzlVWVdKc1pUNE5DaUFnSUNBOFZHRmliR1VnWkdsbVptZHlPbWxrUFNKVVlXSnNaVEkxSWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpJMElqNE5DaUFnSUNBZ0lEemx2SURscDR2bGthZytPRHd2NWJ5QTVhZUw1WkdvUGcwS0lDQWdJQ0FnUE9lN2srYWRuK1dScUQ0NFBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256UThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNNU3d5NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NVkyVjVZK001WkdvUHVXUGpEd3Y1WTJWNVkrTTVaR29QZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqSTFQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTWpZaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU1qVWlQZzBLSUNBZ0lDQWdQT1c4Z09XbmkrV1JxRDR4UEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRThMK2U3aythZG4rV1JxRDROQ2lBZ0lDQWdJRHptbUovbW5KL2xoNkErNXBpZjVweWZORHd2NXBpZjVweWY1WWVnUGcwS0lDQWdJQ0FnUE9XRnQrUzlrK2FYdHVtWHREN25yS3d4TERMb2lvSThMK1dGdCtTOWsrYVh0dW1YdEQ0TkNpQWdJQ0FnSUR6bGpaWGxqNHpsa2FnKzVZMlZQQy9salpYbGo0emxrYWcrRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtNalk4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVeU55SWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSXlOaUkrRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpJOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytNand2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjgwUEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRFc011aUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPV05sZVdQak9XUnFEN2xqNHc4TCtXTmxlV1BqT1dScUQ0TkNpQWdJQ0FnSUR6a3ZiL25sS2ptbHJubHZJOCtjR3M4TCtTOXYrZVVxT2FXdWVXOGp6NE5DaUFnSUNBZ0lEeFNUajR5Tnp3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdJQ0E4VkdGaWJHVWdaR2xtWm1keU9tbGtQU0pVWVdKc1pUSTRJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJakkzSWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK01UWThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTVRZOEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk5Ed3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3hMRExvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHpsalpYbGo0emxrYWcrNVkrTVBDL2xqWlhsajR6bGthZytEUW9nSUNBZ0lDQTg1TDIvNTVTbzVwYTU1YnlQUG5CclBDL2t2Yi9ubEtqbWxybmx2STgrRFFvZ0lDQWdJQ0E4VWs0K01qZzhMMUpPUGcwS0lDQWdJRHd2VkdGaWJHVStEUW9nSUNBZ1BGUmhZbXhsSUdScFptWm5janBwWkQwaVZHRmliR1V5T1NJZ2JYTmtZWFJoT25KdmQwOXlaR1Z5UFNJeU9DSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QakUwUEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRTBQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuelE4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVZMlY1WStNNVpHb1B1V1BqRHd2NVkyVjVZK001WkdvUGcwS0lDQWdJQ0FnUE9TOXYrZVVxT2FXdWVXOGp6NXdhend2NUwyLzU1U281cGE1NWJ5UFBnMEtJQ0FnSUNBZ1BGSk9Qakk1UEM5U1RqNE5DaUFnSUNBOEwxUmhZbXhsUGcwS0lDQWdJRHhVWVdKc1pTQmthV1ptWjNJNmFXUTlJbFJoWW14bE16QWlJRzF6WkdGMFlUcHliM2RQY21SbGNqMGlNamtpUGcwS0lDQWdJQ0FnUE9XOGdPV25pK1dScUQ0elBDL2x2SURscDR2bGthZytEUW9nSUNBZ0lDQTg1N3VUNXAyZjVaR29Qak04TCtlN2srYWRuK1dScUQ0TkNpQWdJQ0FnSUR6bW1KL21uSi9saDZBKzVwaWY1cHlmTkR3djVwaWY1cHlmNVllZ1BnMEtJQ0FnSUNBZ1BPV0Z0K1M5aythWHR1bVh0RDduckt3eExETG9pb0k4TCtXRnQrUzlrK2FYdHVtWHRENE5DaUFnSUNBZ0lEemxqWlhsajR6bGthZys1WTJWUEMvbGpaWGxqNHpsa2FnK0RRb2dJQ0FnSUNBODVMMi81NVNvNXBhNTVieVBQbkJyUEMva3ZiL25sS2ptbHJubHZJOCtEUW9nSUNBZ0lDQThVazQrTXpBOEwxSk9QZzBLSUNBZ0lEd3ZWR0ZpYkdVK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVXpNU0lnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0l6TUNJK0RRb2dJQ0FnSUNBODVieUE1YWVMNVpHb1BqazhMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrT1R3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4MFBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JERXNNdWlLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1dObGVXUGpPV1JxRDdsalpVOEwrV05sZVdQak9XUnFENE5DaUFnSUNBZ0lEemt2Yi9ubEtqbWxybmx2STgrY0dzOEwrUzl2K2VVcU9hV3VlVzhqejROQ2lBZ0lDQWdJRHhTVGo0ek1Ud3ZVazQrRFFvZ0lDQWdQQzlVWVdKc1pUNE5DaUFnSUNBOFZHRmliR1VnWkdsbVptZHlPbWxrUFNKVVlXSnNaVE15SWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpNeElqNE5DaUFnSUNBZ0lEemx2SURscDR2bGthZytORHd2NWJ5QTVhZUw1WkdvUGcwS0lDQWdJQ0FnUE9lN2srYWRuK1dScUQ0MFBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256UThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNNU3d5NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NVkyVjVZK001WkdvUHVXUGpEd3Y1WTJWNVkrTTVaR29QZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqTXlQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTXpNaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU16SWlQZzBLSUNBZ0lDQWdQT1c4Z09XbmkrV1JxRDR4TVR3djVieUE1YWVMNVpHb1BnMEtJQ0FnSUNBZ1BPZTdrK2FkbitXUnFENHhNVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjgwUEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRFc011aUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPV05sZVdQak9XUnFEN2xqWlU4TCtXTmxlV1BqT1dScUQ0TkNpQWdJQ0FnSUR6a3ZiL25sS2ptbHJubHZJOCtjR3M4TCtTOXYrZVVxT2FXdWVXOGp6NE5DaUFnSUNBZ0lEeFNUajR6TXp3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdJQ0E4VkdGaWJHVWdaR2xtWm1keU9tbGtQU0pVWVdKc1pUTTBJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJak16SWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK01USThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTVRJOEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk5Ed3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3hMRExvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHpsalpYbGo0emxrYWcrNVkrTVBDL2xqWlhsajR6bGthZytEUW9nSUNBZ0lDQTg1TDIvNTVTbzVwYTU1YnlQUG5CclBDL2t2Yi9ubEtqbWxybmx2STgrRFFvZ0lDQWdJQ0E4VWs0K016UThMMUpPUGcwS0lDQWdJRHd2VkdGaWJHVStEUW9nSUNBZ1BGUmhZbXhsSUdScFptWm5janBwWkQwaVZHRmliR1V6TlNJZ2JYTmtZWFJoT25KdmQwOXlaR1Z5UFNJek5DSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QakU4TCtXOGdPV25pK1dScUQ0TkNpQWdJQ0FnSUR6bnU1UG1uWi9sa2FnK01UWThMK2U3aythZG4rV1JxRDROQ2lBZ0lDQWdJRHptbUovbW5KL2xoNkErNXBpZjVweWZORHd2NXBpZjVweWY1WWVnUGcwS0lDQWdJQ0FnUE9XRnQrUzlrK2FYdHVtWHREN25yS3d6TERUb2lvSThMK1dGdCtTOWsrYVh0dW1YdEQ0TkNpQWdJQ0FnSUR6a3ZiL25sS2ptbHJubHZJOCtjR3M4TCtTOXYrZVVxT2FXdWVXOGp6NE5DaUFnSUNBZ0lEeFNUajR6TlR3dlVrNCtEUW9nSUNBZ1BDOVVZV0pzWlQ0TkNpQWdJQ0E4VkdGaWJHVWdaR2xtWm1keU9tbGtQU0pVWVdKc1pUTTJJaUJ0YzJSaGRHRTZjbTkzVDNKa1pYSTlJak0xSWo0TkNpQWdJQ0FnSUR6bHZJRGxwNHZsa2FnK01Ud3Y1YnlBNWFlTDVaR29QZzBLSUNBZ0lDQWdQT2U3aythZG4rV1JxRDR4TUR3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4MFBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JEVXNOdWlLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqTTJQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTXpjaUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU16WWlQZzBLSUNBZ0lDQWdQT1c4Z09XbmkrV1JxRDR4UEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRXlQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuelE4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTnl3NDZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVMMi81NVNvNXBhNTVieVBQbkJyUEMva3ZiL25sS2ptbHJubHZJOCtEUW9nSUNBZ0lDQThVazQrTXpjOEwxSk9QZzBLSUNBZ0lEd3ZWR0ZpYkdVK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVXpPQ0lnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0l6TnlJK0RRb2dJQ0FnSUNBODVieUE1YWVMNVpHb1BqRThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTlR3djU3dVQ1cDJmNVpHb1BnMEtJQ0FnSUNBZ1BPYVluK2FjbitXSG9EN21tSi9tbko4MVBDL21tSi9tbkovbGg2QStEUW9nSUNBZ0lDQTg1WVczNUwyVDVwZTI2WmUwUHVlc3JERXNNdWlLZ2p3djVZVzM1TDJUNXBlMjZaZTBQZzBLSUNBZ0lDQWdQT1M5ditlVXFPYVd1ZVc4ano1d2F6d3Y1TDIvNTVTbzVwYTU1YnlQUGcwS0lDQWdJQ0FnUEZKT1BqTTRQQzlTVGo0TkNpQWdJQ0E4TDFSaFlteGxQZzBLSUNBZ0lEeFVZV0pzWlNCa2FXWm1aM0k2YVdROUlsUmhZbXhsTXpraUlHMXpaR0YwWVRweWIzZFBjbVJsY2owaU16Z2lQZzBLSUNBZ0lDQWdQT1c4Z09XbmkrV1JxRDQ0UEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRTBQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuelU4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVMMi81NVNvNXBhNTVieVBQbkJyUEMva3ZiL25sS2ptbHJubHZJOCtEUW9nSUNBZ0lDQThVazQrTXprOEwxSk9QZzBLSUNBZ0lEd3ZWR0ZpYkdVK0RRb2dJQ0FnUEZSaFlteGxJR1JwWm1abmNqcHBaRDBpVkdGaWJHVTBNQ0lnYlhOa1lYUmhPbkp2ZDA5eVpHVnlQU0l6T1NJK0RRb2dJQ0FnSUNBODVieUE1YWVMNVpHb1BqRThMK1c4Z09XbmkrV1JxRDROQ2lBZ0lDQWdJRHpudTVQbW5aL2xrYWcrTVRJOEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk5Ud3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLd3pMRFRvaW9JOEwrV0Z0K1M5aythWHR1bVh0RDROQ2lBZ0lDQWdJRHprdmIvbmxLam1scm5sdkk4K2NHczhMK1M5ditlVXFPYVd1ZVc4ano0TkNpQWdJQ0FnSUR4U1RqNDBNRHd2VWs0K0RRb2dJQ0FnUEM5VVlXSnNaVDROQ2lBZ0lDQThWR0ZpYkdVZ1pHbG1abWR5T21sa1BTSlVZV0pzWlRReElpQnRjMlJoZEdFNmNtOTNUM0prWlhJOUlqUXdJajROQ2lBZ0lDQWdJRHpsdklEbHA0dmxrYWcrTVR3djVieUE1YWVMNVpHb1BnMEtJQ0FnSUNBZ1BPZTdrK2FkbitXUnFENHhNand2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjgxUEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRVc051aUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpReFBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxORElpSUcxelpHRjBZVHB5YjNkUGNtUmxjajBpTkRFaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENDRQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpFeVBDL251NVBtblovbGthZytEUW9nSUNBZ0lDQTg1cGlmNXB5ZjVZZWdQdWFZbithY256VThMK2FZbithY24rV0hvRDROQ2lBZ0lDQWdJRHpsaGJma3ZaUG1sN2JwbDdRKzU2eXNOeXc0NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtOREk4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVME15SWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSTBNaUkrRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytOVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjgxUEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRjc09PaUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpRelBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0FnSUR4VVlXSnNaU0JrYVdabVozSTZhV1E5SWxSaFlteGxORFFpSUcxelpHRjBZVHB5YjNkUGNtUmxjajBpTkRNaVBnMEtJQ0FnSUNBZ1BPVzhnT1duaStXUnFENHhQQy9sdklEbHA0dmxrYWcrRFFvZ0lDQWdJQ0E4NTd1VDVwMmY1WkdvUGpVOEwrZTdrK2FkbitXUnFENE5DaUFnSUNBZ0lEem1tSi9tbkovbGg2QSs1cGlmNXB5Zk5Ud3Y1cGlmNXB5ZjVZZWdQZzBLSUNBZ0lDQWdQT1dGdCtTOWsrYVh0dW1YdEQ3bnJLdzVMREV3NklxQ1BDL2xoYmZrdlpQbWw3YnBsN1ErRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtORFE4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVME5TSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSTBOQ0krRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpnOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytNVEk4TCtlN2srYWRuK1dScUQ0TkNpQWdJQ0FnSUR6bW1KL21uSi9saDZBKzVwaWY1cHlmTlR3djVwaWY1cHlmNVllZ1BnMEtJQ0FnSUNBZ1BPV0Z0K1M5aythWHR1bVh0RDduckt3NUxERXc2SXFDUEMvbGhiZmt2WlBtbDdicGw3UStEUW9nSUNBZ0lDQTg1TDIvNTVTbzVwYTU1YnlQUG5CclBDL2t2Yi9ubEtqbWxybmx2STgrRFFvZ0lDQWdJQ0E4VWs0K05EVThMMUpPUGcwS0lDQWdJRHd2VkdGaWJHVStEUW9nSUNBZ1BGUmhZbXhsSUdScFptWm5janBwWkQwaVZHRmliR1UwTmlJZ2JYTmtZWFJoT25KdmQwOXlaR1Z5UFNJME5TSStEUW9nSUNBZ0lDQTg1YnlBNWFlTDVaR29QakV5UEMvbHZJRGxwNHZsa2FnK0RRb2dJQ0FnSUNBODU3dVQ1cDJmNVpHb1BqRXlQQy9udTVQbW5aL2xrYWcrRFFvZ0lDQWdJQ0E4NXBpZjVweWY1WWVnUHVhWW4rYWNuelk4TCthWW4rYWNuK1dIb0Q0TkNpQWdJQ0FnSUR6bGhiZmt2WlBtbDdicGw3USs1NnlzTVN3eTZJcUNQQy9saGJma3ZaUG1sN2JwbDdRK0RRb2dJQ0FnSUNBODVZMlY1WStNNVpHb1B1V1BqRHd2NVkyVjVZK001WkdvUGcwS0lDQWdJQ0FnUE9TOXYrZVVxT2FXdWVXOGp6NXdhend2NUwyLzU1U281cGE1NWJ5UFBnMEtJQ0FnSUNBZ1BGSk9QalEyUEM5U1RqNE5DaUFnSUNBOEwxUmhZbXhsUGcwS0lDQWdJRHhVWVdKc1pTQmthV1ptWjNJNmFXUTlJbFJoWW14bE5EY2lJRzF6WkdGMFlUcHliM2RQY21SbGNqMGlORFlpUGcwS0lDQWdJQ0FnUE9XOGdPV25pK1dScUQ0eFBDL2x2SURscDR2bGthZytEUW9nSUNBZ0lDQTg1N3VUNXAyZjVaR29Qams4TCtlN2srYWRuK1dScUQ0TkNpQWdJQ0FnSUR6bW1KL21uSi9saDZBKzVwaWY1cHlmTmp3djVwaWY1cHlmNVllZ1BnMEtJQ0FnSUNBZ1BPV0Z0K1M5aythWHR1bVh0RDduckt3eExETG9pb0k4TCtXRnQrUzlrK2FYdHVtWHRENE5DaUFnSUNBZ0lEemt2Yi9ubEtqbWxybmx2STgrY0dzOEwrUzl2K2VVcU9hV3VlVzhqejROQ2lBZ0lDQWdJRHhTVGo0ME56d3ZVazQrRFFvZ0lDQWdQQzlVWVdKc1pUNE5DaUFnSUNBOFZHRmliR1VnWkdsbVptZHlPbWxrUFNKVVlXSnNaVFE0SWlCdGMyUmhkR0U2Y205M1QzSmtaWEk5SWpRM0lqNE5DaUFnSUNBZ0lEemx2SURscDR2bGthZytNVEk4TCtXOGdPV25pK1dScUQ0TkNpQWdJQ0FnSUR6bnU1UG1uWi9sa2FnK01USThMK2U3aythZG4rV1JxRDROQ2lBZ0lDQWdJRHptbUovbW5KL2xoNkErNXBpZjVweWZOand2NXBpZjVweWY1WWVnUGcwS0lDQWdJQ0FnUE9XRnQrUzlrK2FYdHVtWHREN25yS3d6TERUb2lvSThMK1dGdCtTOWsrYVh0dW1YdEQ0TkNpQWdJQ0FnSUR6bGpaWGxqNHpsa2FnKzVZK01QQy9salpYbGo0emxrYWcrRFFvZ0lDQWdJQ0E4NUwyLzU1U281cGE1NWJ5UFBuQnJQQy9rdmIvbmxLam1scm5sdkk4K0RRb2dJQ0FnSUNBOFVrNCtORGc4TDFKT1BnMEtJQ0FnSUR3dlZHRmliR1UrRFFvZ0lDQWdQRlJoWW14bElHUnBabVpuY2pwcFpEMGlWR0ZpYkdVME9TSWdiWE5rWVhSaE9uSnZkMDl5WkdWeVBTSTBPQ0krRFFvZ0lDQWdJQ0E4NWJ5QTVhZUw1WkdvUGpFOEwrVzhnT1duaStXUnFENE5DaUFnSUNBZ0lEem51NVBtblovbGthZytPVHd2NTd1VDVwMmY1WkdvUGcwS0lDQWdJQ0FnUE9hWW4rYWNuK1dIb0Q3bW1KL21uSjgyUEMvbW1KL21uSi9saDZBK0RRb2dJQ0FnSUNBODVZVzM1TDJUNXBlMjZaZTBQdWVzckRNc05PaUtnand2NVlXMzVMMlQ1cGUyNlplMFBnMEtJQ0FnSUNBZ1BPUzl2K2VVcU9hV3VlVzhqejV3YXp3djVMMi81NVNvNXBhNTVieVBQZzBLSUNBZ0lDQWdQRkpPUGpRNVBDOVNUajROQ2lBZ0lDQThMMVJoWW14bFBnMEtJQ0E4TDA1bGQwUmhkR0ZUWlhRK0RRbzhMMlJwWm1abmNqcGthV1ptWjNKaGJUNEw%2BOz4%2BOz47bDxpPDE%2BOz47bDx0PHA8bDxWaXNpYmxlOz47bDxvPHQ%2BOz4%2BO2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BO2k8MTE%2BO2k8MTM%2BO2k8MTU%2BO2k8MTc%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDQ5Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxMDA7Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPEVuYWJsZWQ7PjtsPG88Zj47Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjs%2BPjs%2BHjppMSvSCKU5b1vtWeJJRm99MJo%3D&xiaoq=&jslb=&min_zws=0&max_zws=&Button5=%B0%B4%CC%F5%BC%FE%B2%E9%D1%AF%BD%CC%CA%D2&" +
				"jsbh="+className+"&ddlSyXn="+xuenian+"&ddlSyxq="+xueqi+"&dpDatagrid3%3AtxtChoosePage=1&dpDatagrid3%3AtxtPageSize=100&kssj=212&jssj=212&xqj=2&ddlDsz=%CB%AB&sjd=%271%27%7C%271%27%2C%270%27%2C%270%27%2C%270%27%2C%270%27%2C%270%27%2C%270%27%2C%270%27%2C%270%27&xn=2014-2015&xq=2");
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

	public static  Map<String,String> parseIndexHtml1(String html) throws Exception{
		Map<String,String> urlMap = new HashMap<String, String>();
		Document doc = Jsoup.parse(html);
		Elements links = doc.select("a[href]");
		Element content = doc.getElementById("xhxm");
		String studentname = content.text().substring(0,content.text().lastIndexOf("老"));   //彭小红老师
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
			  // System.out.println(linkText+":"+truelinkHref);
			   urlMap.put(linkText, truelinkHref);
		  }
	     }
		return urlMap;
	}
	/**
	 * 获取主楼，钟海楼的代号，方便发布请求去获取空余教室 ,即获取空余教室的第一步
	 * @param html
	 * @return
	 */
	private static List<ReferName> getValue(String html){
		List<ReferName> referNames = new ArrayList<ReferName>();
		Document doc = Jsoup.parse(html);
		Element select = doc.getElementById("jsbh");
		Elements options = select.getElementsByTag("option");
		for(Element option:options){
			if(option.text().contains("主楼1")){
				continue;
			}
			if(option.text().contains("钟海楼01")){
				continue;
			}
			if(option.text().contains("主楼")||option.text().contains("钟海楼")){
			//	System.out.println(option.text()+"-----"+option.val());
				ReferName fName = new ReferName(option.text(),option.val());
				referNames.add(fName);
			}
			
		}
		return referNames;
	}
	private static String getFreeCourses() throws IOException {
		// 获取请求的真正url
				URL url = new URL(URL);//
				URLConnection conn = url.openConnection();
				conn.connect();
				Map<String, List<String>> headers = conn.getHeaderFields();
				url = conn.getURL();// 获取请求的真正url
				
				baseURL = (url.toString()).substring(0, (url.toString()).lastIndexOf("/")+1);
				//System.out.println(baseURL);
				// 正真的post提交
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("POST");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", "191");
				connection.connect();
				PrintWriter out = new PrintWriter(connection.getOutputStream());
				out.write("__VIEWSTATE=dDwyODE2NTM0OTg7Oz786Ir3TR5s15gT%2BqVlfLAYGJcmdg%3D%3D&txtUserName="
						+ 162014
						+ "&TextBox2="
						+ "XIAOXIAO26"
						+ "&txtSecretCode="
						+ ""
						+ "&RadioButtonList1=%BD%CC%CA%A6&Button1=&lbLanguage=&hidPdrs=&hidsc=");
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
}
