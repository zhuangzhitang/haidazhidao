<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.jkteam.zhidao.domain.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>课程表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
	
	<link rel="stylesheet" type="text/css" href="css/CourseQuery.css">
	<script src="js/jquery-1.8.3.min.js" type="text/javascript"></script>
	<script src="js/CourseQuery.js" type="text/javascript"></script>
	

  </head>
  <body>
    	<table>
    	   <tr >
  			<td style="width:0px" class="timeDay" id="timeDay_1"></td>
  			<td style="width:17%" class="timeDay" id="timeDay_2">一</td>
  			<td style="width:17%" class="timeDay">二</td>
  			<td style="width:17%" class="timeDay">三</td>
  			<td style="width:17%" class="timeDay">四</td>
  			<td style="width:17%" class="timeDay">五</td>
  			<td style="width:17%" class="timeDay">六</td>
  			<td style="width:17%" class="timeDay" id="timeDay_8">日</td>
  		 </tr>
  		

		<% 
		   Map<Integer,Map<Integer,List<Course>>>  map = (Map<Integer,Map<Integer,List<Course>>>)request.getAttribute("map");
			int i = 0;
			for(Map.Entry<Integer,Map<Integer,List<Course>>>  temp: map.entrySet()){
				i++;
				Map<Integer, List<Course>> couseMap = temp.getValue();
				
		 %>
				<tr>
				   <td class="time"><%=(2*i-1) %></td>
					<% 
					boolean flag = true;
					boolean flag1,flag2,flag3,flag4,flag5,flag6,flag0;
					flag1=flag2=flag3=flag4=flag5=flag6=flag0=true;
					for(Map.Entry<Integer, List<Course>> entry:couseMap.entrySet()){
						List<Course> course = entry.getValue();
						Course c = course.get(0);
						
					%>
				    
		  			<% if(c.getDay()==1){ flag1=false;%> <td rowspan="2" class="className " > <div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><% continue;} else if(flag1){%><td rowspan="2" class="className"></td><%flag1= false;} %>
		  			<% if(c.getDay()==2){ flag2=false; %><td rowspan="2" class="className " ><div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><%continue;} else if(flag2){%><td rowspan="2" class="className"></td><%flag2= false;} %>
		  			<% if(c.getDay()==3){ flag3=false;%><td rowspan="2" class="className " ><div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><%continue;} else if(flag3){%><td rowspan="2" class="className"></td><%flag3= false;} %>
		  			<% if(c.getDay()==4){ flag4=false;%><td rowspan="2" class="className " > <div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %></span></div></td><%continue;} else if(flag4){%><td rowspan="2" class="className"></td><%flag4= false;} %>
		  			<% if(c.getDay()==5){ flag5=false;%><td rowspan="2" class="className " > <div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><%continue;} else if(flag5){%><td rowspan="2" class="className"></td><%flag5= false;} %>
		  			<% if(c.getDay()==6){ flag6=false;%><td rowspan="2" class="className " > <div class="Course_Name "><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><%continue;} else if(flag6){%><td rowspan="2" class="className"></td><%flag6= false;} %>
		  			<% if(c.getDay()==0){ flag0=false;%><td rowspan="2" class="className " > <div class="Course_Name"><span class="Cname"><%=c.getCno() %></span><br><span><%=c.getAddress() %> </span></div></td><%continue;} else if(flag0){%><td rowspan="2" class="className"></td><%flag0= false;} %>
		  			
					<% 	
					 
					  
				   }%>
			   </tr>
			
				<tr>
		  			<td class="time" ><%=i*2 %></td>
		  		</tr>
		  
		  <% }%>
	  	
  	</table>
  	
  </body>
</html>
