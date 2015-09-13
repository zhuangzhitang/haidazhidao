package com.jkteam.zhidao.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.query.service.CourseService;

/**
 * 自习教室查询
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class ClassroomQueryServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		CourseService service = new CourseService();
		Map<Integer,List<String>> map = service.getFreeCourse();
		request.setAttribute("zhulou", map.get(1));  //主楼空余教室
		request.setAttribute("zhonghailou", map.get(2));//钟海楼空余教室
		request.getRequestDispatcher("/ClassroomQuery.jsp").forward(request, response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	
}
