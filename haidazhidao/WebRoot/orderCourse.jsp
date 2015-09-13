<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>预定选修课页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="css/orderCourse.css">
	<script src="js/bingingSuccess.js" type="text/javascript"></script>
	<script src="js/jquery-1.8.0.min.js" type="text/javascript"></script>
  </head>  
  <body>
       <div id="main">
     
     <span id="title">亲们，预订选修课吧</span>
      
    <form action="OrderCourseServlet" method="get">
   <!--  <input type="hidden" name="openId" value="${openId}"/>-->
    <div id="user_login">
    	<div id="userName_mian">
    	<span id="course_name">
    	<table>
    		<tr>
    			<td>选修课名称</td>
    		</tr>
    		
    	</table>
    	</span>   
    	<input type="text" name="guanjianzi" placeholder="请输入选修课关键字" id="userName"/>
    	<c:choose>
    	   <c:when test="${param.openId==null}">
    	       <input type="hidden" name="openId" value="${openIdFromWeiXingate}"/>
    	   </c:when>
    	   <c:otherwise>
    	       <input type="hidden" name="openId" value="${param.openId}"/>
    	   </c:otherwise>
    	 </c:choose>
    	</div>
    	
    </div>
    <div id="submit_button">
    	 <input type="submit" value="搜索" id="sum_button">
    </div>
   
     	
      </form>
     </div>
  </body>
</html>
