package com.jkteam.zhidao.query;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.Grade;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.util.CreeperInfos;

/**
 * 成绩查询
 * @author ZheTang
 * @date 2015-5-17
 *
 */
public class ScoreQueryServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String openId = request.getParameter("openId");
		UserService service=new UserService();
		User user = service.queryUser(openId);
		CreeperInfos infos = new CreeperInfos(user.getXuehao(), user.getPassword());//获取课程表
		System.out.println("学号："+user.getXuehao());
		System.out.println("密码："+user.getPassword());
		List<Grade> list =infos.getStudentGrade();
		//System.out.println("------------------list.size:"+list.size());
		request.setAttribute("grades",list);
        request.getRequestDispatcher("/scorequery.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
	
	}

}
