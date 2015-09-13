package com.jkteam.zhidao.base.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.dbutils.handlers.ArrayHandler;

import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;
/**
 * 后台起始起始周数据库操作类
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class StartWeekDao {
   private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
   
   /**
    * 查询起始周
    * @return
    */
   public Calendar queryStartDate(){
	   String sql="SELECT date FROM startWeek WHERE id=1";
	   Date date=(Date)runner.query(sql,new ArrayHandler())[0];
	   Calendar cal=new GregorianCalendar();
	   cal.setTime(date);
	   return cal;
   }
   
  /**
   * 添加起始周
   * @param date
   * @return
   */
   public int insertStartDate(Date date){
	   String sql="UPDATE  startWeek SET date=? WHERE id=1";
	   return runner.update(sql, date);
   }
}
