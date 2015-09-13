package com.jkteam.zhidao.test;

import java.util.List;

import org.junit.Test;

import com.jkteam.zhidao.domain.FreeCourseDB;
import com.jkteam.zhidao.domain.FreeCourseMain;
import com.jkteam.zhidao.query.dao.FreeCourseDao;

public class FreeCourseDaoTest {
	FreeCourseDao dao = new FreeCourseDao();
	@Test
	public void testInsertFreeCourse() {
		FreeCourseDB db = new FreeCourseDB("dfa", "13","fd", "fd");
		//dao.insertFreeCourse(db);
		FreeCourseDao dao = new FreeCourseDao();
		/*List<FreeCourseDB> list = dao.getCoursesOnCurrentTime(1+"", 3+"");
		System.out.println(list.size());*/
	}

}
