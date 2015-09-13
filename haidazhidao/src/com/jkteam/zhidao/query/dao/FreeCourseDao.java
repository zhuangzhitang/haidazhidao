package com.jkteam.zhidao.query.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.FreeCourseDB;
import com.jkteam.zhidao.domain.FreeCourseMain;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;
/**
 * freeCourse表的操作
 * @author ZheTang
 * @date 2015-5-26
 *
 */
public class FreeCourseDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
	public int insertFreeCourse(FreeCourseDB c){
		String sql = "INSERT INTO freeCourse(day,coursenum,week,classname) VALUES(?,?,?,?)";
		return runner.update(sql,c.getDay(),c.getCoursenum(),c.getWeek(),c.getClassname());
	}
	/**
	 * 获取数据库中当期时间段有课的课程
	 * @param weekDay   :星期几
	 * @param coursenum ：第几大节
	 * @return
	 */
	public List<FreeCourseDB> getCoursesOnCurrentTime(int weekDay,int coursenum){
		String sql = "SELECT * FROM freecourse WHERE day=? and coursenum = ?";
		List<FreeCourseDB> list = runner.query(sql, new BeanListHandler<FreeCourseDB>(FreeCourseDB.class), weekDay,coursenum);
		return list;
	}
}
