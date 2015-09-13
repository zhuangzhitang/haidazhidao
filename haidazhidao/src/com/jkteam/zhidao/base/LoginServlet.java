package com.jkteam.zhidao.base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;
/**
 * 后台登录业务类
 * @author 灶鹏
 * @date 2015-5-24
 *
 */
public class LoginServlet extends HttpServlet {

	
	public LoginServlet() {
		super();
	}

	
	public void destroy() {
		super.destroy();
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		UserService userService=new UserService();
		String name=request.getParameter("name");//获取用户名
		String pass=request.getParameter("pass");//获取密码
		if(name!=null&&pass!=null&&!name.equals("")&&!pass.equals("")){
			boolean b=userService.loginHoutai(name, pass);//是否可以登录
			if(b){
				request.getRequestDispatcher("/OpenOptionCourseServlet").forward(request, response);
			}else if(!b){
				request.setAttribute("issuccess","登录失败，请检查用户名或密码");
				request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}
		}else{
			request.setAttribute("issuccess","用户名密码不能为空");
			request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response); 
	   }
	}

	
	public void init() throws ServletException {
		
	}

}
