package com.jkteam.zhidao.base.service;

import java.util.Calendar;

import com.jkteam.zhidao.base.dao.StartWeekDao;
/**
 * 起始周业务类
 * @author 灶鹏
 * @date 2015-5-24
 *
 */
public class StartWeekService {
	private StartWeekDao dao=new StartWeekDao();
	public int insertStartDate(String date){  //2015-05-21 18:28
		String[] ss=date.split(" ");
		String[] ss1=ss[0].split("\\-");
		Calendar cal=Calendar.getInstance();
		cal.set(Integer.parseInt(ss1[0]),Integer.parseInt(ss1[1])-1,Integer.parseInt(ss1[2]));
		return dao.insertStartDate(cal.getTime());
	}
}
