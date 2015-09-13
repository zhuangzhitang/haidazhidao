package com.jkteam.zhidao.query.service;

import java.util.List;

import com.jkteam.zhidao.domain.Exam;
import com.jkteam.zhidao.query.dao.ExamDao;

public class ExamService {
	private ExamDao dao = new ExamDao();
	
	/**
	 * 查询考试通知
	 * @param openId
	 * @return
	 */
	public List<Exam> queryExams(String openId){
		return dao.queryExamsByOpenId(openId);
	}
}
