package com.jkteam.zhidao.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.query.service.CourseService;
import com.jkteam.zhidao.util.CourseparseUtil;
import com.jkteam.zhidao.util.CreeperInfos;
/**
 * 课表查询
 * @author ZheTang
 * @date 2015-5-17
 *
 */
public class CourseQueryServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String openId = request.getParameter("openId");
		CourseService cService = new CourseService();
		
		List<Course> cList = cService.queryByOpenId(openId);
		System.out.println("size():"+cList.size());
		Map<Integer,Map<Integer,List<Course>>> map = CourseparseUtil.toCourse(cList);
		request.setAttribute("map", map);
		request.getRequestDispatcher("/CourseQuery.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		this.doGet(request, response);
	}
}
