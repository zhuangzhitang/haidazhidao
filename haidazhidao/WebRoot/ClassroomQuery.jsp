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
    
    <title>自习教室查询</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	<script src="js/jquery-1.8.3.min.js" type="text/javascript"></script>
	<script src="js/empty.js" type="text/javascript"></script>
	<link rel="stylesheet" type="text/css" href="css/emptyRoom.css">
  </head>
  
  <body>
  	<div id="main">
  	<!-- 主楼 -->
    <h3 class="class_name">主楼</h3>
    <div class="select_main">
  	<select id="select1">
  		<option class="buliding">二楼</option>
  		<option>三楼</option>
  		<option>四楼</option>
  		<option>五楼</option>
  		<option>六楼</option>
  		<option>七楼</option>
  		<option>八楼</option>
  	</select>
  	</div>
  	<!-- 具体教室收集 -->
  	<div id="number" style="display:none">
  		 <c:forEach items="${zhulou}" var="className">
	  		<span class="info">${className}</span>
	  	 </c:forEach>
  	</div>
  	<div class="room">
  		<div class="room2"></div>
  		<div class="room3" style="display:none"></div>
  		<div class="room4" style="display:none"></div>
  		<div class="room5" style="display:none"></div>
  		<div class="room6" style="display:none"></div>
  		<div class="room7" style="display:none"></div>
  		<div class="room8" style="display:none"></div>
  	</div>
  	
  	<!-- 钟海楼 -->
   	<h3 class="class_name">钟海楼</h3>
   	<div class="select_main">
   	<select id="select2">
  		<option>三楼</option>
  		<option>四楼</option>
  		<option>五楼</option>
  		<option>六楼</option>
  	</select>
  	</div>
  	
  		<!-- 具体教室收集 -->
  	<div id="number_1" style="display:none">
  		<c:forEach items="${zhonghailou}" var="className1">
  		  <span class="info_1">${className1}</span>
  		</c:forEach>
  	</div>
  	
  	<div class="room">
  		<div class="room31"></div>
  		<div class="room41" style="display:none"></div>
  		<div class="room51" style="display:none"></div>
  		<div class="room61" style="display:none"></div>
  	</div>
  	</div>
  </body>
</html>
