package com.jkteam.zhidao.Listener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jkteam.zhidao.base.service.RemindTimerTaskService;
/**
 * 当系统启动的时候，监听器自动开启
 * @author 灶鹏
 * @date 2015-5-23
 *
 */
public class RemindHaveClassListener implements ServletContextListener {
   private Logger log=LoggerFactory.getLogger(RemindHaveClassListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("创建定时器线程开始");
	    GregorianCalendar cal=new GregorianCalendar();
	    
		cal.set(Calendar.HOUR_OF_DAY,7);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.MILLISECOND,0);
		new Timer("am").schedule(new RemindTimerTaskService(),cal.getTime());
		
		
		cal.set(Calendar.HOUR_OF_DAY,12);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.MILLISECOND,0);
		new Timer("pm").schedule(new RemindTimerTaskService(),cal.getTime());
		
		cal.set(Calendar.HOUR_OF_DAY,18);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.MILLISECOND,0);
		new Timer("night").schedule(new RemindTimerTaskService(),cal.getTime());
		
		log.info("创建定时器线程结束");

	}

}
