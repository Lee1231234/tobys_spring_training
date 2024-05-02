package com.example.spring;

import com.example.spring.user.dao.UserDaoJdbc;
import com.example.spring.user.domain.User;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
public
class ApplicationTests {

	@Test
	public void andAndGet() throws SQLException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);

		User user = new User();
		user.setId("aaa");
		user.setName("bb");
		user.setPassword("ccc");

		dao.add(user);

		User user2 = dao.get(user.getId());

		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}

	public static void main(String[] args) {
		JUnitCore.main("com.example.spring.ApplicationTests");
	}
}
