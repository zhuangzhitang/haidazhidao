package com.jkteam.zhidao.base.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;



import com.jkteam.zhidao.base.dao.StartWeekDao;
import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.GuangZhuUser;
import com.jkteam.zhidao.query.dao.CourseDao;
import com.jkteam.zhidao.util.SendMessageUtil;


/**
 * 上课提醒业务类
 * @author 灶鹏
 * @date 2015-5-24
 *
 */
 public class RemindTimerTaskService extends TimerTask{
    private StartWeekDao startWeekDao=new StartWeekDao();
    private CourseDao courseDao=new CourseDao();
    
    public RemindTimerTaskService(){}
	public void run() {
		Map<String,List<Course>> openIdCourseMap=new HashMap<String,List<Course>>();
		for(Course c:this.queryCourseByCurrentTime()){
			if(openIdCourseMap.containsKey(c.getOpenId())){
				List<Course> cList=openIdCourseMap.get(c.getOpenId());
				cList.add(c);
				openIdCourseMap.put(c.getOpenId(),cList);
			}else{
				List<Course> newCourseList=new ArrayList<Course>();
				newCourseList.add(c);
				openIdCourseMap.put(c.getOpenId(),newCourseList);
			}
		}
		for(String openId:openIdCourseMap.keySet()){
			List<Course> list=openIdCourseMap.get(openId);
			String content=null;
			for(int i=0;i<list.size();i++){
				Course c=list.get(i);
				if(i==0){
					if(c.getCoursenum()==1||c.getCoursenum()==3){
						 content="您好，您上午需要的课有：\n";
					}else if(c.getCoursenum()==5||c.getCoursenum()==7){
						 content="您好，您下午需要的课有：\n";
					}else{
						 content="您好，您晚上需要的课有：\n";
					}
				}
				content+=c.getCno()+":  \n"+"   上课地点： "+c.getAddress()+"\n"+
				         "   上课时间：第"+c.getCoursenum()+","+(c.getCoursenum()+1)+"小节\n"+
						 "   任课老师："+c.getTeacher()+"\n";
		    }
			System.out.println(content);
			String id=SendMessageUtil.getid(openId);
			SendMessageUtil.isSuccess(id, content);
		}
	}
	/**
	 * 根据当前的时间查询到现在需要上的课
	 * @return 集合
	 */
	public List<Course> queryCourseByCurrentTime(){
		Calendar cal=Calendar.getInstance();
		int day=cal.get(Calendar.DAY_OF_WEEK)-1;
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int[] coursenum=new int[2];
		if(hour<9){
			coursenum[0]=1;
			coursenum[1]=3;
		}else if(hour>=9&&hour<17){
			coursenum[0]=5;
			coursenum[1]=7;
		}else if(hour>=17){
			coursenum[0]=9;
			coursenum[1]=11;
		}
		List<Course> courseList=new ArrayList<Course>();
		courseList=courseDao.queryCurrentTimeCurrent(day, coursenum);
		courseList=this.queryCourseByWeekNum(courseList, this.getCurrentWeek());
		return courseList;
		
	}
	 /**
	  * 获得当前日期的周数
	  * @return
	  */
	public int getCurrentWeek(){
		Calendar startCal=startWeekDao.queryStartDate();
		Calendar currentCal=Calendar.getInstance();
           //currentCal.set(2015,4,18);
		long currenttime=currentCal.getTimeInMillis();
		long starttime=startCal.getTimeInMillis();
		if(currenttime>=starttime){
			long weeknum=(currenttime-starttime)/1000/60/60/24/7+1;
			return (int)weeknum;
		}else{
			return 0;
		}
	}
	/**
	 * 按照课程的周数进行筛选，如果不是当前周该上的课，删除。
	 * @param courseList
	 * @param currentWeekNum  当前的周数
	 * @return
	 */
	private List<Course> queryCourseByWeekNum(List<Course> courseList,int currentWeekNum){
		 List<Course> willDeleteCourse=new ArrayList<Course>();
		 for(Course c:courseList){
			 String weeknum=c.getWeeknum();
			 Set<Integer> weekSet=new HashSet<Integer>();
			 for(String s:weeknum.split("\\|")){
				 String[] allweeknum=s.split("\\-");
				 if(allweeknum.length==1){
					 weekSet.add(Integer.parseInt(allweeknum[0]));
				 }else if(allweeknum.length==2){
					 for(int from=Integer.parseInt(allweeknum[0]);from<=Integer.parseInt(allweeknum[1]);from++){
						 weekSet.add(from);
					 }
				 }
			 }
			 if(!weekSet.contains(currentWeekNum)){
				willDeleteCourse.add(c);
			 }
		 }
		 courseList.removeAll(willDeleteCourse);
		 return courseList;
	}
 }