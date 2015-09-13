package com.jkteam.zhidao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class GetWeiXinPropertiesUtil {
    public static Map<String,String> getProperties(){
    	Map<String,String> proMap=new HashMap<String,String>();
    	InputStream input=GetWeiXinPropertiesUtil.class.getResourceAsStream("/weixin.properties");
    	Properties pro=new Properties();
        try {
			pro.load(input);
			proMap.put("AppID",pro.getProperty("AppID"));
			proMap.put("AppSecret",pro.getProperty("AppSecret"));
			proMap.put("URL",pro.getProperty("URL"));
			proMap.put("Token",pro.getProperty("Token"));
			proMap.put("EncodingAESKey",pro.getProperty("EncodingAESKey"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
    	return proMap;
    }
}
