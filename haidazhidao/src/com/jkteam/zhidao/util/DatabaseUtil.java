package com.jkteam.zhidao.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库工具类: 提供数据库连接 <br/>
 * (使用第三方数据库连接池c3p0, 在src目录下的c3p0-config.xml里配置连接池信息)
 *
 * @author 郭灶鹏
 * 
 */
public class DatabaseUtil {
	//连接池
	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	private static DataSource ds ;
	static{
		 ds = new ComboPooledDataSource();
	}	
	public static DataSource getDataSource(){
		return ds;
	}
	
	/**
	 *  获取Connection
	 */
	public static Connection getConnection(){ 
		//从ThreadLocal中取,确保同一线程取到的是同一个Connection
		Connection con = tl.get();
		if(con==null){
			try {
				con = ds.getConnection();
				//当前线程没有在tl中,存个Connection进去
				tl.set(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return con;
	}
	
   
	public static void remove(){
		tl.remove();
	}
	
	
	//开启事务
	public static void startTransaation(){
		try {
			Connection connection = DatabaseUtil.getConnection();
			connection.setAutoCommit(false);   //开启事务
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	//提交
	public static void commit(){
		try {
			Connection connection = tl.get();
			if(connection!=null){
				connection.commit();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	//回滚
	public static void rollback(){
		try {
			Connection connection = tl.get();
			if(connection!=null){
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	//关闭连接
	public static void close(){
		try {
			Connection connection = tl.get();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}finally{
			DatabaseUtil.remove();
		}
	}
}
