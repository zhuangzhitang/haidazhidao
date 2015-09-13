package com.jkteam.zhidao.other.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


import com.jkteam.zhidao.base.dao.UserDao;
import com.jkteam.zhidao.domain.BookingCourse;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.other.dao.OptionalCourseDao;
import com.jkteam.zhidao.util.SendMessageUtil;

public class OptionCourseTaskService extends TimerTask {
	private OptionalCourseDao dao=new OptionalCourseDao();
	private UserDao userDao=new UserDao();
	@Override
	public void run() {
		// TODO Auto-generated method stub
       List<BookingCourse> courseList=dao.queryAllBookingCourse();
       Map<User,List<BookingCourse>> map=new HashMap<User,List<BookingCourse>>();
       for(BookingCourse c:courseList){
    	   User u=userDao.queryUser(c.getOpenId());
    	   if(map.containsKey(u)){
    		   map.get(u).add(c);
    	   }else{
    		   List<BookingCourse> list=new ArrayList<BookingCourse>();
    		   list.add(c);
    		   map.put(u, list);
    	   }
       }
      
       for(User user:map.keySet()){
    	   for(BookingCourse bc:map.get(user)){
    		  if(dao.queryOCourseById(bc.getCid()).getType()==0){
    			  boolean b=this.selectSportsCourse(user, bc);
    			  if(b){
    				  String name=dao.queryOCourseById(bc.getCid()).getName();
    				  SendMessageUtil.isSuccess(SendMessageUtil.getid(user.getOpenId()),"您预订的选修课："+name+"\n选课成功，请登录教务系统查看");
    				  dao.deleteBookingCourse(bc.getId());
    			  }
    		  }else{
    			  boolean b=this.selectOpenCourse(user, bc);
    			  if(b){
    				  String name=dao.queryOCourseById(bc.getCid()).getName();
    				  SendMessageUtil.isSuccess(SendMessageUtil.getid(user.getOpenId()),"您预订的选修课："+name+"\n选课成功，请登录教务系统查看");
    				  dao.deleteBookingCourse(bc.getId());
    			  }
    		  }
    	   }
       }
	}
	/**
	 * 选取体育课
	 * @param user  用户
	 * @param course  预订的选修课对象
	 * @return
	 */
   public boolean selectSportsCourse(User user,BookingCourse course){
	   return false;
   }
   /**
    * 选取校公选课  （等到选课的时候，学校教务系统开放选课时，才有实现的可能）
    * @param user
    * @param course
    * @return
    * 为了选课的效率，必须实现一次登录，实现多次选课
    */
   public boolean selectOpenCourse(User user,BookingCourse course){
	   return false;
   }
}
