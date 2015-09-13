package com.jkteam.zhidao.test;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jkteam.zhidao.util.DataSafeModeUtil;
import com.jkteam.zhidao.util.WXBizMsgCrypt;


public class JiamiAndJiemiTest {
   public static void main(String[] args) {
		//
		// 第三方回复公众平台
		//

		// 需要加密的明文
				String encodingAesKey = "EnuvfrCiAX9lq1s6gSKPKuD3wmg68I7fujss4cPLZN8";
				String token = "weixinCourse";
				String timestamp = "1409304348";
				String nonce = "zhaozhifeng";
				String appId = "wx7b0956652346e53c";
		String replyMsg = "<xml><ToUserName><![CDATA[haidazhidao]]></ToUserName><FromUserName><![CDATA[gzp_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[这是一个测试]]></Content><MsgId>1234567890123456</MsgId></xml>";

		String jiami=DataSafeModeUtil.encryptData(replyMsg);
        


		//
		// 公众平台发送消息给第三方，第三方处理
		//

		// 第三方收到公众号平台发送的消息
	//	String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
		//System.out.println("解密后明文: " + result2);
}
}
