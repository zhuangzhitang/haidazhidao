package com.jkteam.zhidao.util;

import java.io.Writer;

import com.jkteam.zhidao.domain.Articles;
import com.jkteam.zhidao.domain.ResponseImageTextMessage;
import com.jkteam.zhidao.domain.ResponseTextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class MessageToXmlUtil {
	 private static XStream xstream = new XStream(new XppDriver() {  
	        public HierarchicalStreamWriter createWriter(Writer out) {  
	            return new PrettyPrintWriter(out) {  
	                // 对所有xml节点的转换都增加CDATA标记  
	                boolean cdata = true;  
	                @SuppressWarnings("rawtypes")
					public void startNode(String name, Class clazz) {  
	                    super.startNode(name, clazz);  
	                }  
	  
	                protected void writeText(QuickWriter writer, String text) {  
	                    if (cdata) {  
	                        writer.write("<![CDATA[");  
	                        writer.write(text);  
	                        writer.write("]]>");  
	                    } else {  
	                        writer.write(text);  
	                    }  
	                }  
	            };  
	        }  
	    });  
	   /** 
	     * 文本消息对象转换成xml 
	     *  
	     * @param textMessage 文本消息对象 
	     * @return xml 
	     */  
	    public static String textMessageToXml(ResponseTextMessage textMessage) {  
	        xstream.alias("xml", textMessage.getClass());  
	        return xstream.toXML(textMessage);  
	    }  
	    
	    /** 
	     * 图文消息对象转换成xml 
	     *  
	     * @param newsMessage 图文消息对象 
	     * @return xml 
	     */  
	    public static String newsMessageToXml(ResponseImageTextMessage newsMessage) {  
	        xstream.alias("xml", newsMessage.getClass());  
	        xstream.alias("item", new Articles().getClass());  
	        return xstream.toXML(newsMessage);  
	    }  
}
