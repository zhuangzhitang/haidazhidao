package com.jkteam.zhidao.query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.util.CreeperInfos;

/**
 * 教务通知
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class NoticeServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String openId = request.getParameter("openId");
		UserService service=new UserService();
		User user = service.queryUser(openId);
		CreeperInfos infos = new CreeperInfos(user.getXuehao(), user.getPassword());//获取课程表
	    String tongzhi =  infos.getTonggou();//获取教务通知
	    if(tongzhi == null || tongzhi.equals("")){
	    	tongzhi = "预祝广东海洋大学80年校庆举办成功!!!!";
	    }
	    request.setAttribute("tongzhi", tongzhi);
		request.getRequestDispatcher("/Notice.jsp").forward(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		this.doGet(request, response);
	}

}
