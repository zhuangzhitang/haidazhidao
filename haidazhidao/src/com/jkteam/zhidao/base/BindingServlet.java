package com.jkteam.zhidao.base;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.query.service.CourseService;
import com.jkteam.zhidao.util.CreeperInfos;
/**
 * 处理有关用户绑定与解绑的servlet类
 * @author 郭灶鹏
 *
 */
public class BindingServlet extends HttpServlet {
    private UserService userService=new UserService();

	public BindingServlet() {
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
		String openId=request.getParameter("openId");
		if(userService.isbinding(openId)){
			request.setAttribute("issuccess","您的账号已经绑定，无需再绑定了");
			request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
		}else{
		 request.setAttribute("openId",openId);
		 request.getRequestDispatcher("/bindingUser.jsp").forward(request, response);
		}
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8"); 
		response.setCharacterEncoding("UTF-8"); 
		String xuehao=request.getParameter("xuehao");
		String mima=request.getParameter("mima");
		HttpSession session = (HttpSession) request.getSession();
		
		System.out.println("学号："+xuehao);
		System.out.println("姓名："+mima);
		String openId=request.getParameter("openId");
		//String nickName=(String)request.getAttribute(openId);
		User user=new User(openId, xuehao, mima, 0,1,"");
		/**
		 * 此处在插入数据库之前还必须进入教务系统进行账号和密码验证，还需插入代码。
		 */
		CreeperInfos infos = new CreeperInfos(user.getXuehao(), user.getPassword());//获取课程表
		if(infos.isLonginSuccess()){
			int i=userService.bindingUser(user);
			if(i>0){
				//查询成绩
				List<Course> cList = infos.getStudentCource();
				CourseService cService = new CourseService();
				cService.insertUser(cList, openId);
				request.setAttribute("issuccess","绑定成功");
			}else{
				request.setAttribute("issuccess","绑定失败");
			}
		}else{
			request.setAttribute("issuccess","绑定失败");
		}
		request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
	}

	
	public void init() throws ServletException {
		
	}

}
