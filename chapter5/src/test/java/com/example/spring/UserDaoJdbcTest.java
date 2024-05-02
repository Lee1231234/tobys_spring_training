package com.example.spring;

import com.example.spring.user.dao.UserDao;
import com.example.spring.user.dao.UserDaoJdbc;
import com.example.spring.user.domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.dao.DuplicateKeyException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoJdbcTest {
	@Autowired
	DataSource dataSource;
	@Autowired
	private UserDao dao;
	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() {


		this.user1 = new User("gyumee", "1", "springno1");
		this.user2 = new User("leegw700", "2", "springno2");
		this.user3 = new User("bumjin", "3", "springno3");

	}
	@Test
	public void andAndGet() throws SQLException {

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));

		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
	}

	@Test(expected= EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.get("unknown_id");
	}
	@Test
	public void count() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		assertThat(dao.getCount(), is(1));

		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	@Test
	public void getAll()  {
		dao.deleteAll();

		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));

		dao.add(user1); // Id: gyumee
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));

		dao.add(user2); // Id: leegw700
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));

		dao.add(user3); // Id: bumjin
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
	}

	@Test(
			expected = DuplicateKeyException.class
	)
	public void duplciateKey() {
		this.dao.deleteAll();
		this.dao.add(this.user1);
		this.dao.add(this.user1);
	}
	@Test
	public void sqlExceptionTranslate() {
		this.dao.deleteAll();

		try {
			this.dao.add(this.user1);
			this.dao.add(this.user1);
		} catch (DuplicateKeyException var5) {
			SQLException sqlEx = (SQLException)var5.getCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			DataAccessException transEx = set.translate((String)null, (String)null, sqlEx);
			Assert.assertThat(transEx, CoreMatchers.is(DuplicateKeyException.class));
		}

	}
//	int maxtry = MAX_RETRY;
//	while(maxtry -- > 0){
//		try{
//			return;
//		}catch (SomeException e){
//			//로그 출력과  정해진 시간만큼 대기
//		}finally {
//			// 리소스 반납, 정리작업
//		}
//	}
//	throw new RetryFailedException();

//	public void  add() throws SQLException{
//		try {
//
//		}catch (SQLException e){
//			throw e;
//		}
//	}
//	public void add(User user) throws DuplicateUserIdException, SQLException{
//		try{
//			// 실행코드
//		}catch (SQLException e){
//			if(e.getErrorCode()== MysqlErrorNumbers.ER_DUP_ENTRY)
//				throw DuplicateUserIdException();
//			else
//				throw e;
//		}
//	}
	public static void main(String[] args){
		JUnitCore.main("com.example.spring.user.dao.UserDaoTest");
	}
}