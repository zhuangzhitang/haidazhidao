package com.jkteam.zhidao.base.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;

/**
 * 关于User表的数据库操作
 * @author 郭灶鹏
 *
 */
public class UserDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
	/**
	 * 判断是否绑定
	 * @param openId 微信Id
	 * @return  true 已经绑定
	 */
    public boolean isbinding(String openId){
    	String sql="SELECT status FROM user WHERE openId=?";
    	Object[] object=runner.query(sql,new ArrayHandler(),openId);
    	if(object==null||(Integer)object[0]==1){
    		return false;
    	}else if((Integer)object[0]==0){
    		return true;
    	}
    	return false;
    }
    /**
     * 是否开启上课提醒
     * @param openId 微信Id
     * @return
     */
    public boolean isclassWarn(String openId){
    	String sql="SELECT classwarn FROM user WHERE openId=?";
    	Object[] o=runner.query(sql,new ArrayHandler(),openId);
    	if((Integer)o[0]==0){
    		return false;
    	}else{
    		return true;
    	}
    }
    /**
     * 取消绑定
     * @author zhetang 修改，实现级联删除
     * @param openId
     * @return
     */
    public int removeBinding(String openId){
    	String sql="delete from user where openId = ?";
    	return runner.update(sql, openId);
    }
    /**
     * 开启或者关闭上课提醒
     * @param openId
     * @param classWarn  0为关闭上课提醒，1为开启上课提醒
     * @return
     */
    public int openOrCloseClassWarn(String openId,int classWarn){
    	String sql="UPDATE user SET classwarn=? WHERE openId=?";
    	return runner.update(sql, classWarn,openId);
    }
    /**
     * 根据openID查询出User
     * @param openId
     * @return
     */
    public User queryUser(String openId){
    	String sql="SELECT * FROM user WHERE openId=?";
    	return runner.query(sql,new BeanHandler<User>(User.class),openId);
    }
    
    public List<User> queryUser(){
    	String sql="SELECT * FROM user";
    	return runner.query(sql,new BeanListHandler<User>(User.class));
    }
    
    public int updateUserStatus(String openId){
    	String sql="UPDATE user SET status=0 WHERE openId=?";
    	return runner.update(sql, openId);
    }
    /**
     * 添加用户，即绑定用户操作
     * @param user
     * @return
     */
    public int insertUser(User user){
    	String sql="INSERT INTO user(openId,nickName,xuehao,password,status,classwarn) VALUES(?,?,?,?,?,?)";
    	return runner.update(sql,user.getOpenId(),user.getNickName(),user.getXuehao(),user.getPassword(),user.getStatus(),user.getClasswarn());
    }
    
    /**
     * 是否存在该用户
     * @param name   用户名
     * @param pass   密码
     * @return     false ：失败   true ：成功
     */
    public boolean loginHoutai(String name,String pass){
    	String sql="SELECT username FROM houtaiUser WHERE username=? AND pass=?";
    	Object[] o=runner.query(sql,new ArrayHandler(),name,pass);
    	if(o==null){
    		return false;
    	}
    	if(o.length==1){
    		return true;
    	}
    	return false;
    }
}
