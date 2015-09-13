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
    
    <title>绑定页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
	<script src="js/bingingSuccess.js" type="text/javascript"></script>
	<script src="js/jquery-1.8.0.min.js" type="text/javascript"></script>
	<style>
	
		*{
			font-family:Georgia, "Times New Roman", Times, serif;
		}
		
		#main{
			text-align:center;
			margin-top:39%;
			width:100%;
		}
		#user_login{
			width:100%;
			margin:0px auto;
			margin-top:30%;
			text-align:center;
		}
		
		span{
			 font-size:130%;
			
		}
		#userName,#password{
			border:1px solid #ccc;
			background-color:#fff;
			border-radius:10px;
			-moz-border-radius:10px;
			-o-border-radius:10px;
			-webkit-border-radius:10px;
			height:35px;;
			width:70%;
			-webkit-box-shadow: 0 2px 10px rgba(0,0,0,0.2);
			   -moz-box-shadow: 0 2px 10px rgba(0,0,0,0.2);
			    -ms-box-shadow: 0 2px 10px rgba(0,0,0,0.2);
				 -o-box-shadow: 0 2px 10px rgba(0,0,0,0.2);
			        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
			 outline:medium;
			 padding-left:5px;
		}
		#userName_mian,#password_main{
			margin-bottom:10px;
		}
		#submit_button{
			width:90%;
			margin:0 auto;
			text-align:center
			
			
		}
		#sum_button{
			
			width:100%;
			height:35px;
			border:1px solid #ccc;
			border-radius:3px;
			-moz-border-radius:3px;
			-o-border-radius:3px;
			-webkit-border-radius:3px;
			 outline:medium;
			 background-color:#6c2;
			 color:#fff;
			 letter-spacing:5px;
			 font-size:130%;
		}


	</style>
  </head>  
  <body> 
  
     <c:if test="${requestScope.message!=null}">
     <span>${requestScope.message}</span>
     </c:if>
    <form action="BindingServlet" method="post">
    <input type="hidden" name="openId" value="${openId}"/>
    <div id="user_login">
    	<div id="userName_mian">
    	<span style="font-family:"Times New Roman",Georgia,Serif;">学号:</span>   
    	<input type="text" name="xuehao" placeholder="请输入学号" id="userName"/>
    	</div>
    	
    	<div id="password_main">
    	<span>密码:</span>
    	 <input type="password" name="mima" placeholder="请输入密码" id="password"/>
    	 </div>
    </div>
    <div id="submit_button">
    	 <input type="submit" value="绑定" id="sum_button">
    </div>
   
     	
      </form>
    
  </body>
</html>
