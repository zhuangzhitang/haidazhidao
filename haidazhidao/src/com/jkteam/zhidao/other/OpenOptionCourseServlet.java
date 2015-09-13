package com.jkteam.zhidao.other;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jkteam.zhidao.base.service.StartWeekService;
import com.jkteam.zhidao.base.service.UserService;
import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.User;
import com.jkteam.zhidao.other.service.OpenOptionCourseService;
import com.jkteam.zhidao.other.service.OrderCourseService;
import com.jkteam.zhidao.query.service.CourseService;
import com.jkteam.zhidao.util.CreeperInfos;

/**
 * @author 灶鹏
 * @date 2015-5-22
 *后台操作Serlvet
 */
public class OpenOptionCourseServlet extends HttpServlet {
   private OpenOptionCourseService openService=new OpenOptionCourseService();
   private OrderCourseService orderService=new OrderCourseService();
   private UserService userService=new UserService();
   private CourseService cService = new CourseService();
   private StartWeekService sService=new StartWeekService();

	public OpenOptionCourseServlet() {
		super();
	}

	
	public void destroy() {
		super.destroy(); 
		
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String haha=request.getParameter("haha");
		if(haha==null){
		    if(OpenOptionCourseService.timer==null){
		    	request.setAttribute("openOrClose","1");  //开启定时器
		    }else{
		    	request.setAttribute("openOrClose","0");  //关闭定时器
		    }
		    request.getRequestDispatcher("/timer.jsp").forward(request, response);
		}else{
			if(haha.equals("0")){  //关闭选修
				openService.closeTimer();
				request.setAttribute("issuccess","关闭定时器成功");
				request.setAttribute("openOrClose","1");
				request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}else if(haha.equals("1")){ //开启选修课
				String time=request.getParameter("time");
				if(time.equals("")){
					request.setAttribute("issuccess","时间不能为空");
				}else{
					openService.createTimer(openService.parserTime(time),1000*60*60*2);
					request.setAttribute("issuccess","开启定时器成功");
				}
			   request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}else if(haha.equals("2")){
				String startweek=request.getParameter("startweek");   //2015-05-21 18:28
				int i=sService.insertStartDate(startweek);
				if(i==1){
					request.setAttribute("issuccess","设置起始周成功");
				}else if(i==0){
					request.setAttribute("issuccess","设置起始周失败");
				}
				request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}else if(haha.equals("3")){
				cService.deleteAllCourse();
				List<User> userList=userService.queryUser();
				for(User user:userList){
					CreeperInfos infos = new CreeperInfos(user.getXuehao(), user.getPassword());
					List<Course> cList = infos.getStudentCource();
					int i=cService.insertUser(cList,user.getOpenId());
					if(i==0){
						request.setAttribute("issuccess","重新抓取课程失败");
						request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
					}
				}
				request.setAttribute("issuccess","重新抓取课程成功");
				request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}else if(haha.equals("4")){
				int i[]=orderService.insertOpenAndSportCourse();
				if(i[0]>0&&i[1]>0){
					request.setAttribute("issuccess","重新获取选修课名字成功");
				}else{
					request.setAttribute("issuccess","重新获取选修课名字失败");
				}
				request.getRequestDispatcher("/bindingSuccess.jsp").forward(request, response);
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doGet(request, response);
	}

	
	public void init() throws ServletException {
		
	}

}
