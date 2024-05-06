package com.example.spring.user.Service;

import com.example.spring.user.dao.UserDao;
import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"/test-applicationContext.xml"}
)
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    MailSender mailSender;
    @Autowired
    PlatformTransactionManager transactionManager;
    List<User> users;

    public UserServiceTest() {
    }

    @Before
    public void setUp() {
        this.users = Arrays.asList(new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, 49, 0), new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, 50, 0), new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, 29), new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, 30), new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        this.userDao.deleteAll();
        Iterator var2 = this.users.iterator();

        while(var2.hasNext()) {
            User user = (User)var2.next();
            this.userDao.add(user);
        }

        MockMailSender mockMailSender = new MockMailSender();
        this.userService.setMailSender(mockMailSender);
        this.userService.upgradeLevels();
        this.checkLevelUpgraded((User)this.users.get(0), false);
        this.checkLevelUpgraded((User)this.users.get(1), true);
        this.checkLevelUpgraded((User)this.users.get(2), false);
        this.checkLevelUpgraded((User)this.users.get(3), true);
        this.checkLevelUpgraded((User)this.users.get(4), false);
        List<String> request = mockMailSender.getRequests();
        Assert.assertThat(request.size(), CoreMatchers.is(2));
        Assert.assertThat((String)request.get(0), CoreMatchers.is(((User)this.users.get(1)).getEmail()));
        Assert.assertThat((String)request.get(1), CoreMatchers.is(((User)this.users.get(3)).getEmail()));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = this.userDao.get(user.getId());
        if (upgraded) {
            Assert.assertThat(userUpdate.getLevel(), CoreMatchers.is(user.getLevel().nextLevel()));
        } else {
            Assert.assertThat(userUpdate.getLevel(), CoreMatchers.is(user.getLevel()));
        }

    }

    @Test
    public void add() {
        this.userDao.deleteAll();
        User userWithLevel = (User)this.users.get(4);
        User userWithoutLevel = (User)this.users.get(0);
        userWithoutLevel.setLevel((Level)null);
        this.userService.add(userWithLevel);
        this.userService.add(userWithoutLevel);
        User userWithLevelRead = this.userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = this.userDao.get(userWithoutLevel.getId());
        Assert.assertThat(userWithLevelRead.getLevel(), CoreMatchers.is(userWithLevel.getLevel()));
        Assert.assertThat(userWithoutLevelRead.getLevel(), CoreMatchers.is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() {
        UserService testUserService = new TestUserService(((User)this.users.get(3)).getId(), (TestUserService)null);
        testUserService.setUserDao(this.userDao);
        testUserService.setTransactionManager(this.transactionManager);
        testUserService.setMailSender(this.mailSender);
        this.userDao.deleteAll();
        Iterator var3 = this.users.iterator();

        while(var3.hasNext()) {
            User user = (User)var3.next();
            this.userDao.add(user);
        }

        try {
            testUserService.upgradeLevels();
            Assert.fail("TestUserServiceException expected");
        } catch (TestUserServiceException var4) {
        }

        this.checkLevelUpgraded((User)this.users.get(1), false);
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList();

        MockMailSender() {
        }

        public List<String> getRequests() {
            return this.requests;
        }

        public void send(SimpleMailMessage mailMessage) throws MailException {
            this.requests.add(mailMessage.getTo()[0]);
        }

        public void send(SimpleMailMessage[] mailMessage) throws MailException {
        }
    }

    static class TestUserService extends UserService {
        private String id;

        private TestUserService(String id, TestUserService testUserService) {
            this.id = id;
        }

        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            } else {
                super.upgradeLevel(user);
            }
        }
    }

    static class TestUserServiceException extends RuntimeException {
        TestUserServiceException() {
        }
    }
}
