package com.jkteam.zhidao.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 这个类用来获取当前时间所对应的星期几，第几大节
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class DateUtil {
	/**
	 * 返回年份
	 * @return
	 */
	public static String getYear(){
		Calendar calendar=Calendar.getInstance(); 
		return String.valueOf(calendar.get(Calendar.YEAR));
	}
	/**
	 * 返回当前正处在第几大节的上课时间段
	 * @param date
	 * @return
	 */
	public static int getCoursenum(Date date){
		int hour = date.getHours();
		System.out.println("hour:"+hour);
		if(hour<10){
			return 1;
		}else if(hour>=10&&hour<12){
			return 3;
		}else if(hour>=12 && hour<16){
			return 5;
		}else if(hour>=16 && hour<18){
			return 7;
		}else{
			return 9;
		}
	}
	
	/**
	 * @param date  :当前日期
	 * @return  当前星期对应的代号： 星期一：1；星期二：2；星期三：3；星期一：四；..星期天：0
	 */
	public static int getWeekDay(Date date){
		  Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (w < 0)
	            w = 0;
	       return w;
	}
}
