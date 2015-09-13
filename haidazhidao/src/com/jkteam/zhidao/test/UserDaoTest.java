package com.jkteam.zhidao.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jkteam.zhidao.base.dao.UserDao;

public class UserDaoTest {
	UserDao dao = new UserDao();
	@Test
	public void testRemoveBinding() {
		dao.removeBinding("orfG4jn9-kif3l1v_TXf6gxZKrcg");
	}

}
