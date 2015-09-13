package com.jkteam.zhidao.other.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.jkteam.zhidao.domain.BookingCourse;
import com.jkteam.zhidao.domain.OCourse;
import com.jkteam.zhidao.domain.OptionalCourse;
import com.jkteam.zhidao.util.CreeperInfos;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;
/**
 * 选修课表数据库操作类
 * @author 灶peng
 * @date 2015-4-27
 *
 */
public class OptionalCourseDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
    public int[] insertOpenAndSportCourse(){
    	String sql1="DELETE FROM bookingcourse";
    	runner.update(sql1);
    	sql1="DELETE FROM optionalcourse";
    	runner.update(sql1);
    	int i[]=new int[2];
    	CreeperInfos c=new CreeperInfos("201211621310","889798guozaopeng");
    	Set<OptionalCourse> optionalCourseSet=c.getSchoolOpenSource();
    	Set<String> sportsSet=c.getSchoolSportName();
    	for(OptionalCourse oc:optionalCourseSet){
    	  String sql="INSERT INTO optionalcourse(name,type) VALUES (?,1)";
    	  int num= runner.update(sql,oc.getOptioncName());
    	  if(num>0){
    		  i[0]++;
    	  }
    	}
    	for(String s:sportsSet){
    	  String sql="INSERT INTO optionalcourse(name,type) VALUES (?,0)";
       	  int num= runner.update(sql,s);
       	  if(num>0){
       		  i[1]++;
       	  }
    	}
    	return i;
    }
	public List<OCourse> findOpenCourse(String guanjianzi) {
		String sql="SELECT * FROM optionalcourse WHERE name LIKE ?";
		List<OCourse> o=runner.query(sql,new BeanListHandler<OCourse>(OCourse.class),"%"+guanjianzi+"%");
		if(o!=null){
			return o;
		}else{
			return null;
		}
	}
	public boolean insertOrderCourse(String openId, String courseName, int week,
			int time) {
		String sql="SELECT * FROM optionalcourse WHERE name=?";
		OCourse o=runner.query(sql,new BeanHandler<OCourse>(OCourse.class),courseName);
		if(o!=null){
			int cid=o.getId();
			sql="INSERT INTO bookingcourse(openId,cid,day,coursenum) VALUES (?,?,?,?)";
			int i=runner.update(sql,openId,cid,week,time);
			if(i==1){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	public List<BookingCourse> queryAllBookingCourse(){
		String sql="SELECT * FROM bookingcourse WHERE issuccess=0";
		return runner.query(sql,new BeanListHandler<BookingCourse>(BookingCourse.class));
	}
    
	public OCourse queryOCourseById(int id){
		String sql="SELECT * FROM optionalcourse WHERE id=?";
		OCourse o=runner.query(sql,new BeanHandler<OCourse>(OCourse.class),id);
		return o;
	}
	public void deleteBookingCourse(int id){
		String sql="DELETE FROM bookingcourse WHERE id=?";
		runner.update(sql,id);
	}
	public void deleteAllBookingCourse(){
		String sql="DELETE FROM bookingcourse";
		runner.update(sql);
	}
}
