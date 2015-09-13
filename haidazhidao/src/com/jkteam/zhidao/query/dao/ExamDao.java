package com.jkteam.zhidao.query.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.jkteam.zhidao.domain.Course;
import com.jkteam.zhidao.domain.Exam;
import com.jkteam.zhidao.util.DatabaseUtil;
import com.jkteam.zhidao.util.MyQueryRunner;
/**
 * 考试查询
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class ExamDao {
	private MyQueryRunner runner=new MyQueryRunner(DatabaseUtil.getDataSource());
	
	public List<Exam> queryExamsByOpenId(String openId){
		String sql = "SELECT * FROM exam WHERE openId = ?";
		List<Exam> exams = runner.query(sql, new BeanListHandler<Exam>(Exam.class),openId);
		return exams;
	}
}
