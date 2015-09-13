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
    
    <title>成绩表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
	<link rel="stylesheet" type="text/css" href="css/scorequery.css">
	<script src="js/jquery-1.8.0.min.js" type="text/javascript"></script>
	<script src="js/scorequery.js"  type="text/javascript"></script>

  </head>
  
  <body>
   
    <c:forEach items="${grades}" var="grade">
    <div id="main">
	   <div class="id1">
	   		<div class="id2">
	   			<table class="table1">
	   				<tr>
	   					<td>${grade.courseName}</td>
	   				</tr>
	   			</table>	
			</div>
	   		<div class="id3">
	   			<table class="table2">
	   				<tr>
	   					<td>
	   						<span class="point">学分:</span><span>${grade.credit }</span><br>
	   						<span class="score">成绩:</span><span>${grade.score }</span><br>
	   						<span class="point-score">绩点:</span><span>${grade.gradePoint}</span>
	   					</td>
	   				</tr>
	   			</table>
	   		</div>
	   </div>
	</c:forEach> 
	</div>
  </body>
</html>
