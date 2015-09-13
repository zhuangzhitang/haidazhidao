package com.jkteam.zhidao.test;

import java.util.List;

import org.junit.Test;

import com.jkteam.zhidao.domain.Exam;
import com.jkteam.zhidao.query.dao.ExamDao;

public class ExamDaoTest {
	ExamDao dao = new ExamDao();
	@Test
	public void testQueryExamsByOpenId() {
		List<Exam> list = dao.queryExamsByOpenId("orfG4jn9-kif3l1v_TXf6gxZKrcg");
		for(Exam exam :list){
			System.out.println(exam.toString());
		}
	}

}
