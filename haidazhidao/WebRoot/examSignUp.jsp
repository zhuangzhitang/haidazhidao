<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>等级考试页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style>
		table{
			width:100%;
			height:100%;
			text-align:center;
			vertical-align:middle;
		}
	</style>
  </head>
  
  <body>
  		<table>
  			<tr>
  				<td><h1>此阶段不是报名时间</h1><br></td>
  			</tr>
  		</table>
         <%-- <input type="hidden" name="openId" value="${param.openId}"/>
         openID=${param.openId} --%>
         
  </body>
</html>
