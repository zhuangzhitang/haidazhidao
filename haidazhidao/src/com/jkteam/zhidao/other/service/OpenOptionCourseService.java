package com.jkteam.zhidao.other.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

import com.jkteam.zhidao.other.dao.OptionalCourseDao;
/**
 * 公共选修课业务类
 * @author 灶peng
 * @date 2015-4-26
 *
 */
public class OpenOptionCourseService {
    public static Timer timer;
    private OptionalCourseDao dao=new OptionalCourseDao();
    
    public void createTimer(Date date,long period){
    	timer=new Timer();
    	timer.schedule(new OptionCourseTaskService(),date, period);
    }
    public void closeTimer(){
    	dao.deleteAllBookingCourse();
    	timer.cancel();
    	timer=null;
    }
    
    /**
     * 解析时间
     * @param time
     * @return
     */
    public Date parserTime(String time){  //2015-05-21 18:28
    	String[] ss=time.split(" ");
    	String[] ss1=ss[0].split("\\-");
    	String[] ss2=ss[1].split(":");
    	GregorianCalendar cal=new GregorianCalendar();
    	cal.set(Integer.parseInt(ss1[0]),Integer.parseInt(ss1[1])-1,
    			Integer.parseInt(ss1[2]),Integer.parseInt(ss2[0]),
    			Integer.parseInt(ss2[1]));
       return cal.getTime();
    }
}
