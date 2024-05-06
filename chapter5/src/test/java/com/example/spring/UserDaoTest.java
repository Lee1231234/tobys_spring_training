package com.example.spring;

import com.example.spring.user.Service.UserService;
import com.example.spring.user.dao.UserDao;
import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
    @Autowired
    UserDao dao;
    @Autowired
    DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    public UserDaoTest() {
    }

    @Before
    public void setUp() {
        this.user1 = new User("gyumee", "박성철", "springno1", "user1@ksug.org", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "springno2", "user2@ksug.org", Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "박범진", "springno3", "user3@ksug.org", Level.GOLD, 100, 40);
    }

    @Test
    public void andAndGet() {
        this.dao.deleteAll();
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(0));
        this.dao.add(this.user1);
        this.dao.add(this.user2);
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(2));
        User userget1 = this.dao.get(this.user1.getId());
        this.checkSameUser(userget1, this.user1);
        User userget2 = this.dao.get(this.user2.getId());
        this.checkSameUser(userget2, this.user2);
    }

    @Test(
            expected = EmptyResultDataAccessException.class
    )
    public void getUserFailure() throws SQLException {
        this.dao.deleteAll();
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(0));
        this.dao.get("unknown_id");
    }

    @Test
    public void count() {
        this.dao.deleteAll();
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(0));
        this.dao.add(this.user1);
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(1));
        this.dao.add(this.user2);
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(2));
        this.dao.add(this.user3);
        Assert.assertThat(this.dao.getCount(), CoreMatchers.is(3));
    }

    @Test
    public void getAll() {
        this.dao.deleteAll();
        List<User> users0 = this.dao.getAll();
        Assert.assertThat(users0.size(), CoreMatchers.is(0));
        this.dao.add(this.user1);
        List<User> users1 = this.dao.getAll();
        Assert.assertThat(users1.size(), CoreMatchers.is(1));
        this.checkSameUser(this.user1, (User)users1.get(0));
        this.dao.add(this.user2);
        List<User> users2 = this.dao.getAll();
        Assert.assertThat(users2.size(), CoreMatchers.is(2));
        this.checkSameUser(this.user1, (User)users2.get(0));
        this.checkSameUser(this.user2, (User)users2.get(1));
        this.dao.add(this.user3);
        List<User> users3 = this.dao.getAll();
        Assert.assertThat(users3.size(), CoreMatchers.is(3));
        this.checkSameUser(this.user3, (User)users3.get(0));
        this.checkSameUser(this.user1, (User)users3.get(1));
        this.checkSameUser(this.user2, (User)users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        Assert.assertThat(user1.getId(), CoreMatchers.is(user2.getId()));
        Assert.assertThat(user1.getName(), CoreMatchers.is(user2.getName()));
        Assert.assertThat(user1.getPassword(), CoreMatchers.is(user2.getPassword()));
        Assert.assertThat(user1.getEmail(), CoreMatchers.is(user2.getEmail()));
        Assert.assertThat(user1.getLevel(), CoreMatchers.is(user2.getLevel()));
        Assert.assertThat(user1.getLogin(), CoreMatchers.is(user2.getLogin()));
        Assert.assertThat(user1.getRecommend(), CoreMatchers.is(user2.getRecommend()));
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

    @Test
    public void update() {
        this.dao.deleteAll();
        this.dao.add(this.user1);
        this.dao.add(this.user2);
        this.user1.setName("오민규");
        this.user1.setPassword("springno6");
        this.user1.setEmail("user6@ksug.org");
        this.user1.setLevel(Level.GOLD);
        this.user1.setLogin(1000);
        this.user1.setRecommend(999);
        this.dao.update(this.user1);
        User user1update = this.dao.get(this.user1.getId());
        this.checkSameUser(this.user1, user1update);
        User user2same = this.dao.get(this.user2.getId());
        this.checkSameUser(this.user2, user2same);
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
