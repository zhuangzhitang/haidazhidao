package com.jkteam.zhidao.util;

import java.util.Map;

/**
 * 微信安全模式下的对数据的加密和解密处理
 * @author 郭灶鹏
 *
 */
public class DataSafeModeUtil {
   private static String encodingAesKey;
   private static String token;
   private static String appId;
   //用于加密数据的时间戳，随机数
   private static String timestamp="1409304348";
   private static String nonce="zhaozhifeng";
   
   static{
	  Map<String,String> proMap=GetWeiXinPropertiesUtil.getProperties();
	  encodingAesKey=proMap.get("EncodingAESKey");
	  token=proMap.get("Token");
      appId=proMap.get("AppID");	  
   }
   
   /**
    * 加密数据
    * @param msg 需要加密的数据
    * @return  加密后的数据
    */
   public static String encryptData(String msg){
	   try {
		 WXBizMsgCrypt pc=new WXBizMsgCrypt(token, encodingAesKey, appId);
		 return pc.encryptMsg(msg, timestamp, nonce);
	  } catch (AesException e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	  }  
   }
   /**
    * 解密数据
    * @param msg   需要解密的数据
    * @param msgSignature   微信签名，可通过request.getParamer("msgSignature")得到
    * @param timestamp      时间戳，同上
    * @param nonce          随机数，同上
    * @return      解密后的数据
    */
   public static String decryptData(String msg,String msgSignature,String timestamp,String nonce){
	 WXBizMsgCrypt pc=null;
	try {
		pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
		return pc.decryptMsg(msgSignature, timestamp, nonce, msg);
	} catch (AesException e){
		e.printStackTrace();
		throw new RuntimeException(e);
	}
   }
}
