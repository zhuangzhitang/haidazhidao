package com.jkteam.zhidao.query.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;
/**
 * 课表dao
 * @author ZheTang
 * @date 2015-5-11
 *
 */
public class CourseDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
	
	/**
	 * 根据openId（微信唯一标志号）删除绑定的课程
	 * @param opendId
	 * @return
	 */
	public int  deleteCourseByOpendId(String opendId){
		String sql="DELETE FROM course where openId = ?";
    	return runner.update(sql, opendId);
	}
	
	/**
	 * 根据openId（微信唯一标志号）返回课程表
	 * @param openId
	 * @return
	 */
	public List<Course> queryByOpenId(String openId){
		String sql = "SELECT * FROM course WHERE openId=?" ;
		List<Course> list = runner.query(sql, new BeanListHandler<Course>(Course.class),openId);
		return list;
	}
	
	/**
	 * 存储用户对应的课程表
	 * @param course
	 * @return
	 */
	public int insertUser(Course course){
    	String sql = "INSERT INTO course(cno,weeknum,day,coursenum,address,teacher,openId) VALUES(?,?,?,?,?,?,?)";
    	return runner.update(sql,course.getCno(),course.getWeeknum(),
    			course.getDay(),course.getCoursenum(),course.getAddress(),
    			course.getTeacher(),course.getOpenId());
    }
   public int deleteAllCourse(){
	   String sql="DELETE FROM course";
	   return runner.update(sql);
   }
	
	public List<Course> queryCurrentTimeCurrent(int day,int[] coursenum){
		String sql="SELECT c.* FROM course c,user u WHERE c.openId=u.openId AND u.status=0 AND u.classwarn=1 AND c.day=? AND c.coursenum IN (?,?)";
		return runner.query(sql,new BeanListHandler<Course>(Course.class),day+"",coursenum[0]+"",coursenum[1]+"");
	}

	
	
	/**
	 * 获取数据库中当期时间段有课的课程
	 * @param day   :星期几
	 * @param coursenum ：第几大节
	 * @return
	 */
	public List<Course> getCoursesOnCurrentTime(int day,int coursenum){
		String sql = "SELECT * FROM course WHERE day=? and coursenum = ?";
		List<Course> list = runner.query(sql, new BeanListHandler<Course>(Course.class), day,coursenum);
		return list;
	}
	

}
