package com.jkteam.zhidao.base.dao;



import java.util.Date;

import org.apache.commons.dbutils.handlers.BeanHandler;

import com.jkteam.zhidao.domain.AccessToken;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;

/**
 * 对AccessToken表的数据库操作
 * @author 郭灶鹏
 *
 */
public class AccessTokenDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
    public AccessToken queryAccessToken(){
    	AccessToken accessToken=null;
    	String sql="SELECT * FROM accessToken WHERE id=1";
    	accessToken=runner.query(sql,new BeanHandler<AccessToken>(AccessToken.class));
    	int now=(int) (new Date().getTime()/1000);
    	if(now-accessToken.getTime()<accessToken.getExpires_in()){
    		return accessToken;
    	}else{
    		return null;
    	}
    }
   
    public void updateAccessToken(AccessToken a){
    	String sql="UPDATE accessToken SET accesstoken=?,expires_in=?,time=? WHERE id=?";
    	runner.update(sql, new Object[]{a.getAccesstoken(),a.getExpires_in(),a.getTime(),1});
    }
}
