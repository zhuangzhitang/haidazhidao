package com.jkteam.zhidao.base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;



/**
 * 微信服务器转发请求到项目的入口，BaseServlet主要的功能是对来自用户的请求进行判断分析，
 * 并且对用户的请求作出对应的响应。
 * @author 郭灶鹏
 *
 */
public class BaseServlet extends HttpServlet {

	public BaseServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(request.getParameter("echostr"));
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	   request.setCharacterEncoding("UTF-8"); 
	   response.setCharacterEncoding("UTF-8"); 
       UserService replyService=new UserService();
       replyService.processMesssageFromWeixin(request, response);
       
	}
	public void init() throws ServletException {
		// Put your code here
	}

}
