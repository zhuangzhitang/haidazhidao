package com.jkteam.zhidao.other;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.domain.OCourse;
import com.jkteam.zhidao.other.service.OrderCourseService;
/**
 * 选修课搜索Servlet
 * @author灶鹏
 * @date 2015-4-29
 *
 */
public class OrderCourseServlet extends HttpServlet {
	private OrderCourseService service=new OrderCourseService();


	public OrderCourseServlet() {
		super();
	}


	public void destroy() {
		super.destroy(); 
		
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 request.setCharacterEncoding("UTF-8");
	     response.setCharacterEncoding("UTF-8");
        this.doPost(request, response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
	    request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    String guanjianzi=new String(request.getParameter("guanjianzi").getBytes("ISO-8859-1"),"UTF-8");
	    String openId=new String(request.getParameter("openId").getBytes("ISO-8859-1"),"UTF-8");
	    request.setAttribute("openId",openId);
	    List<OCourse> o=service.findOpenCourse(guanjianzi);
	    if(o.size()!=0){
	     request.setAttribute("courseName",o);
	     request.getRequestDispatcher("/orderCourse1.jsp").forward(request, response);
	    }else{
	    	request.getSession().setAttribute("message","");
	    	request.setAttribute("issuccess","查无该选修课，请返回重新搜索");
	    	request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
	    }
	}

	
	public void init() throws ServletException {
		
	}

}
