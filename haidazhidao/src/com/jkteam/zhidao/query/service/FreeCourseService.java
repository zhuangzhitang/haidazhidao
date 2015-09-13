package com.jkteam.zhidao.query.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.FreeCourseDB;
import com.jkteam.zhidao.domain.FreeCourseMain;
import com.jkteam.zhidao.query.dao.FreeCourseDao;
import com.jkteam.zhidao.util.CreeperInfos;
import com.jkteam.zhidao.util.DateUtil;
import com.jkteam.zhidao.util.FreeCourse;

public class FreeCourseService {
	private FreeCourseDao dao = new FreeCourseDao();
	public int truebitInsertFreeCourse(){
		int flag = 0;
		Map<String,String> map = readFileForFreeCourse();
		for(Map.Entry<String, String> entry:map.entrySet()){
			String[] temp = entry.getKey().split("\\|");
			String classname = temp[0];
			String day = temp[1];
			String num = temp[2];
			String weeks = entry.getValue();
		    FreeCourseDB db = new FreeCourseDB(classname, day, num, weeks);
		    flag = dao.insertFreeCourse(db);
		}
		return flag;
	}
	public int bitAddUnFreeCourses(){
		List<FreeCourseMain> list = CreeperInfos.getTotalUnFreeCourse("2014-2015", "2");
		int flag = 0;
		writeToFile(list);
		return flag;
	}
	public boolean writeToFile(List<FreeCourseMain> list){
		boolean flag = false;
		PrintWriter out =null;
		try {
		    out= new PrintWriter("c://abc.txt");
			for (FreeCourseMain c :list) {
				out.println(c.getStarWeek()+"|"+c.getEndWeek()+"|"+c.getDay()+"|"+c.getCoursenum()+"|"+c.getWeek()+"|"+c.getClassName());
			}
			flag = true;
		} catch (FileNotFoundException e) {
			flag = false;
		}finally{
			out.close();
		}
		return flag;
	}
	
	public Map<String,String> readFileForFreeCourse(){
		BufferedReader reader = null;
		Map<String,String> map = new HashMap<String, String>();
		try {
			 reader = new BufferedReader(new InputStreamReader(new FileInputStream("c://abc.txt")));
			 String buffer = null;
			 while((buffer=reader.readLine())!=null){
				String[] temp = buffer.split("\\|");
				int startweek = Integer.parseInt(temp[0]);
				int  endweek = Integer.parseInt(temp[1]);
				String day = temp[2];
				String nums = temp[3];
				String zhoushu = temp[4];
				String loucen = temp[5];
				day= dayToIntrefer(day);//星期的标志符
				nums = numsTonumsRefer(nums);//节数的标志符
				StringBuilder cBuilder = new StringBuilder();
				if(zhoushu.equals("单")||zhoushu.equals("双")){
					cBuilder.append(startweek);
				}else{
					for(int i = startweek;i<=endweek;i++){
						if(i==endweek){
							cBuilder.append(i);
						}else{
							cBuilder.append(i+"|");
						}
					}
				}
				String key = loucen+"|"+day+"|"+nums;
				
				if(map.containsKey(key)){
					cBuilder.append("|"+map.get(key));
					map.put(key,cBuilder.toString());
				}else{
					map.put(key, cBuilder.toString());
				}
			 }
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 将星期1 转为1
	 * @param day
	 * @return
	 */
	public String dayToIntrefer(String day){
		String[] temp = {"星期7","星期1","星期2","星期3","星期4","星期5","星期6"};
		for (int i = 0; i < temp.length; i++) {
			if(temp[i] .equals(day))
			{
				return i+"";
			}
		}
		return null;
	}
	public String numsTonumsRefer(String nums){
		String[] temp = {"第1,2节","第3,4节","第5,6节","第7,8节","第9,10节"};
		for (int i = 0; i < temp.length; i++) {
			if(temp[i] .equals(nums))
			{
				int ref = 2*i+1;
				return ref+"";
			}
		}
		return null;
	}
	
	/**
	 * 返回教室名
	 * 
	 * @return  Map<Integer, List<String>>    key为1,：表示 主楼的空余教室  key 为2 :表示钟海楼的空余教室
	 */
	public Map<Integer, List<String>> getFreeCourse(){
		Date date = new Date();
		int weekDay = DateUtil.getWeekDay(date);//获取星期
		int coursenum = DateUtil.getCoursenum(date);//获取第几节
		
		List<FreeCourseDB> courses = dao.getCoursesOnCurrentTime(weekDay,coursenum);
		Map<Integer, List<String>> map = null;
		if(courses !=null){
			
		}
		
		return map;
	}
	public static void main(String[] args) throws Exception {
		/*FreeCourseService service = new FreeCourseService();
		Map<String,String> map = service.readFileForFreeCourse();
		for(Map.Entry<String, String> entry:map.entrySet()){
			System.out.println(entry.getKey()+"---"+entry.getValue());
		}*/
	//	System.out.println(map.size());
		FreeCourseService service = new FreeCourseService();
		//service.truebitInsertFreeCourse();
		Date date = new Date();
		int weekDay = DateUtil.getWeekDay(date);//获取星期
		int coursenum = DateUtil.getCoursenum(date);//获取第几节
		FreeCourseDao dao = new FreeCourseDao();
		List<FreeCourseDB> list = dao.getCoursesOnCurrentTime(1, 3);
		for(FreeCourseDB db:list){
			System.out.println(db.getClassname());
		}
	}
}
