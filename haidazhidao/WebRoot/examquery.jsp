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
    
    <title>考试安排</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
     <link rel="stylesheet" type="text/css" href="css/testArramge.css">
  </head>
  
  <body>
  
     
  	 
  	 <table cellspacing="1">
    	<tr>
    		<td style="width:40%;height:40px;text-align:center" class="info"><span style="font-size:20px;">科目</span></td>
    		<td style="width:60%;height:40px;text-align:center" class="info"><span style="font-size:20px;">考试时间</span></td>
    	</tr>
    	<c:forEach items="${exams}" var="exam">
    	<tr >
    		<td class="info info_1">${exam.examName}</td>
    		<td class="info info_1">${exam.examTime }</td>
    	</tr>
    	</c:forEach>
    </table>
  </body>
</html>
