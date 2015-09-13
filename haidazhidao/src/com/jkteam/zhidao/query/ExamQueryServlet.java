package com.jkteam.zhidao.query;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.domain.Exam;
import com.jkteam.zhidao.query.service.ExamService;
/**
 * 考试安排查询
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class ExamQueryServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String openId = request.getParameter("openId");
		ExamService service = new ExamService();
		List<Exam> exams = service.queryExams(openId);
		request.setAttribute("exams",exams);
		request.getRequestDispatcher("/examquery.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		this.doGet(request, response);
	}

}
