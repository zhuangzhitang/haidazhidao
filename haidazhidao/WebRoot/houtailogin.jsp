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
    
    <title>后台管理员登录</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
	<link rel="stylesheet" type="text/css" href="css/houtailogin.css">
	<script src="js/bingingSuccess.js" type="text/javascript"></script>
	<script src="js/jquery-1.8.0.min.js" type="text/javascript"></script>
  </head>  
  <body> 
  
    <form action="LoginServlet" method="post">
    <input type="hidden" name="openId" value="${openId}"/>
    <div id="user_login">
    	<div id="userName_mian">
    	<span style="font-family:"Times New Roman",Georgia,Serif;">用户名:</span>   
    	<input type="text" name="name" placeholder="请输入用户名" id="userName"/>
    	</div>
    	<div id="password_main">
    	<span id="word">密码:</span>
    	 <input type="password" name="pass" placeholder="请输入密码" id="password"/>
    	 </div>
    </div>
    <div id="submit_button">
    	 <input type="submit" value="登录" id="sum_button">
    </div>
      </form>
    
  </body>
</html>
