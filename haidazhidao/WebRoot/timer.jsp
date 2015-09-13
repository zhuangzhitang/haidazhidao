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
    

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	<link rel="stylesheet" type="text/css" href="css/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/icon.css">
    <link rel="stylesheet" type="text/css" href="css/generic.css">
    <script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui-lang-zh_CN.js"></script>
	<link rel="stylesheet" type="text/css" href="css/timer.css">
	
	<script type="text/javascript">
	  $(function(){
	     $('#dt').datetimebox({  

          showSeconds:false  
      }); 
       $('#ds').datetimebox({  

          showSeconds:false  
          }); 
      }); 
	  
	</script>
  </head>
  <body>
     <div id="content">
     <div id="class_begin">
     <form action="OpenOptionCourseServlet" method="post" id="form">
     <c:if test="${openOrClose=='1'}">
        <input type="hidden" value="1" name="haha"/>
	           开启选课时间：<input id="dt" type="text" name="time"/> 
	         
	    <input type="submit" id="xuanke" value="开启"/>
	</c:if>
	<c:if test="${openOrClose=='0'}">
	     <input type="hidden" value="0" name="haha"/>
	    关闭选课时间： <input type="submit" id="xuanke" value="关闭"/>
	</c:if>
	</form>
	</div>
	<div id="week_beigin">
		<form action="OpenOptionCourseServlet">
			 <input type="hidden" value="2" name="haha"/>
			   选择学期起始周:<input id="ds" type="text" name="startweek"/> 
			 <input type="submit" id="xuanke" value="确定"/>
		</form>
		</div>	
			<div class="button_main">
			<form action="OpenOptionCourseServlet">
			 <input type="hidden" value="3" name="haha"/>
			<input type="submit" value="重新获取课表" class="button >
			</form>
			</div>
			
			<div class="button_main">
			<form action="OpenOptionCourseServlet" > 
			 <input type="hidden" value="4" name="haha"/>
			  <input type="submit" value="重新获取选修课名字" class="button">
			  </div>
	    </form>
	</div>
	
	
	
     
  
      
  </body>
</html>
