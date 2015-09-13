package com.jkteam.zhidao.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.OptionalCourse;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.util.CreeperInfos;

/**
 * 处理已选选修课
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class AlreadyOptionalCourseServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String openId = request.getParameter("openId");
		UserService service=new UserService();
		User user = service.queryUser(openId);
		CreeperInfos infos = new CreeperInfos(user.getXuehao(), user.getPassword());//获取已选选修课
		List<OptionalCourse> opcourses = infos.getYixuanGongxuangke();
		Set<OptionalCourse> schoolOptioncCourses = infos.getSchoolOpenSource();//校公选课
		
		System.out.println("size:"+schoolOptioncCourses.size());
		request.setAttribute("opcourses",opcourses);
		request.setAttribute("schoolOptioncCourses", schoolOptioncCourses);
		request.getRequestDispatcher("AlreadyOptionalCourse.jsp").forward(request, response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		this.doGet(request, response);
	}
	
}
