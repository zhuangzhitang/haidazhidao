package com.jkteam.zhidao.base.service;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.jkteam.zhidao.base.dao.AccessTokenDao;
import com.jkteam.zhidao.domain.AccessToken;
import com.jkteam.zhidao.util.GetWeiXinPropertiesUtil;
import com.jkteam.zhidao.util.WeiXinConnectionUtil;

/**
 *  管理Access_token的Service类
 * @author 郭灶鹏
 *
 */
public class AccessTokenService {
	private Logger log=LoggerFactory.getLogger(AccessToken.class);
	private AccessTokenDao accessDao=new AccessTokenDao();
	/**
	 * 获取Accesstoken值，如果数据库所存的AccessToken已经失效，重新获取
	 * @return  AccessToken值
	 */
    public String getAccessToken(){
    	AccessToken accessToken=accessDao.queryAccessToken();
    	if(accessToken==null){
    	   accessToken=this.getAccessTokenFromWeiXin();
    	   accessDao.updateAccessToken(accessToken);
         }
    	return accessToken.getAccesstoken();
    }
    /**
     * 通过get请求调用接口，从微信获得新的AccessToken
     * @return  AccessToken对象 
     */
    private AccessToken getAccessTokenFromWeiXin(){
        AccessToken accessToken=null;
    	String requesturl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    	String s=requesturl.replace("APPID",GetWeiXinPropertiesUtil.getProperties().get("AppID"))
    	          .replace("APPSECRET",GetWeiXinPropertiesUtil.getProperties().get("AppSecret"));
    	JSONObject jsonObect=WeiXinConnectionUtil.httpRequest(s,"GET",null);
    	if(jsonObect!=null){
    	  try {
			  String accesstoken=jsonObect.getString("access_token");
			  int expires_in=jsonObect.getInt("expires_in");
			  Date date=new Date();
			  int time=(int) (date.getTime()/1000);
			  accessToken=new AccessToken(accesstoken, expires_in, time);
		} catch (JSONException e) {
			  log.error("获取Accesstoken失败 errcode:{} errmsg:{}",jsonObect.getInt("errcode"),jsonObect.getString("errmsg"));
		}
    	}
    	return accessToken;
    }

}
