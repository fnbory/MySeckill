package com.fnbory.miaosha.service;

import com.fnbory.miaosha.domain.User;
import com.fnbory.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;


@Service
public class UserService {
	
//	@Autowired
//	UserDao userDao;
//
//	public User getById(int id) {
//		 return userDao.getById(id);
//	}
//
//	@Transactional
//	public boolean tx() {
//		User u1= new User();
//		u1.setId(2);
//		u1.setName("2222");
//		userDao.insert(u1);
//
//		User u2= new User();
//		u2.setId(1);
//		u2.setName("11111");
//		userDao.insert(u2);
//
//		return true;
//	}
//
//    public void login(HttpServletResponse response, LoginVo loginVo) {
//    }
}
