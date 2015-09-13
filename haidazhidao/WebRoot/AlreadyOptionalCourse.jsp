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
    
    <title>公共选课查询</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
    <link rel="stylesheet" type="text/css" href="css/selectScore.css">
	<script src="js/jquery-1.8.3.min.js" type="text/javascript"></script>
	<script src="js/selectScore.js"></script>
  </head>
  
  <body>
  
  	 	<span class="title">你的选修课情况</span>
  	<br>
  	<!-- 选修课信息 -->
   		<table id="table1" cellspacing="0">
   			<tr  id="header">
   				<td style="height:30px;width:500px;">&nbsp;&nbsp;&nbsp;选修课名</td>
   			</tr>
   			<c:forEach items="${opcourses}" var="opcourse">
	   			<tr class="course" >
	   				<td class="scoreName" id="scoreName">
	   				<span class="play play1"  ></span>
	   					${opcourse.optioncName}
	   					<div class="info">
	   					<span>教师:</span><span>${opcourse.teacher }</span><br>
	   					<span>时间:</span><span>${opcourse.time }</span><br>
	   					<span>地点:</span><span>${opcourse.address }</span><br>
	   					</div>
	   				</td>
	   				
	   			</tr>
   	        </c:forEach>
   		</table>
   		<br><br>
   		
   		<span class="title">全校选修情况 </span>
   		<!-- 全校选修课信息 -->
   		<table id="table2" cellspacing="0">
   			<tr id="header">
   				<td style="height:30px;width:500px;left:130px">&nbsp;&nbsp;&nbsp;选修课名</td>
   			</tr>
   			<tbody id="tbody1">
   			  <c:forEach items="${schoolOptioncCourses}" var="opcourse1">
	   			<tr class="course" >
	   				<td class="scoreName" id="scoreName">
	   				<h4 class="play play1"></h4>
	   					${opcourse1.optioncName}
	   					<div class="info">
	   					<span>教师:</span><span>${opcourse1.teacher }</span><br>
	   					<span>时间:</span><span>${opcourse1.time }</span><br>
	   					<span>地点:</span><span>${opcourse1.address }</span><br>
	   					</div>
	   				</td>
	   				
	   			</tr>
   		   	  </c:forEach>
   			</tbody>
   		</table>
  </body>
</html>
