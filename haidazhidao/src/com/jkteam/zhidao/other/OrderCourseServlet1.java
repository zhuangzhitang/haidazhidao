package com.jkteam.zhidao.other;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.other.service.OrderCourseService;
/**
 * 预定选修课的Servlet
 * @author 灶peng
 * @date 2015-5-23
 *
 */
public class OrderCourseServlet1 extends HttpServlet {
	private OrderCourseService service=new OrderCourseService();

	
	public OrderCourseServlet1() {
		super();
	}

	
	public void destroy() {
		super.destroy(); 
		
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		 request.setCharacterEncoding("UTF-8");
	     response.setCharacterEncoding("UTF-8");
		this.doPost(request, response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String openId=request.getParameter("openId");
		String courseName=(String)request.getParameter("courseName");
		System.out.println(courseName);
	    courseName=new String(courseName.getBytes("ISO-8859-1"),"UTF-8");
		int week=Integer.parseInt(request.getParameter("week"));
		int time=Integer.parseInt(request.getParameter("time"));
		request.getSession().setAttribute("message","");
		if(service.insertOrderCourse(openId, courseName, week, time)){
			request.setAttribute("issuccess","预订选修课成功，选课成功后我们会通过微信通知您");
		}else{
			request.setAttribute("issuccess","预订选修课失败，请查看是否有该课程名");
		}
		request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
	}


	public void init() throws ServletException {
		// Put your code here
	}

}
