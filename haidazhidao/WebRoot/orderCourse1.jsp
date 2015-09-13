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
    
    <title>预订选修课</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
	<link rel="stylesheet" type="text/css" href="css/orderCourseMain.css">
	

  </head>
  
  <body>
  <div id="main">
    <form action="OrderCourseServlet1" method="get">
        <input type="hidden" value="${openId}" name="openId" >
    	<ul>
    		<li>
    		<span>名称</span>
    		<span class="search_name">
    		   <select name="courseName">
    		      <c:forEach var="course" items="${courseName}">
    		          <option value="${course.name}">${course.name}</option>
    		      </c:forEach>
    		   </select>
    		</span>
    		</li>
    		<li>
    			<span id="time">时间</span>
    			<span class="search_name">
    			<select name="week" >
    			    <option value="0">周日</option>
    				<option value="1">周一</option>
    				<option value="2">周二</option>
    				<option value="3">周三</option>
    				<option value="4">周四</option>
    				<option value="5">周五</option>
    				<option value="6">周六</option>
    			</select>
    			</span>
    		</li>
    		<li><span id="number_class">节数</span>
    			<span class="search_name">
    				<select name="time" >
    					<option value="1">第1,2大节</option>
    					<option value="2">第3,4大节</option>
    					<option value="3">第5,6大节</option>
    					<option value="4">第7,8大节</option>
    					<option value="5">第9,10大节</option>
    				</select>
    			</span>
    		</li>
    	</ul>
    	<div id="button">
    		<input type="submit" value="预定选修课" id="buttonMain">
    	</div>
    </form>
    </div>
  </body>
</html>
