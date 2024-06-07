package com.example.spring.user.Service;

import com.example.spring.user.dao.UserDao;
import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"/test-applicationContext.xml"}
)
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserService testUserService;
    @Autowired
    UserDao userDao;
    @Autowired
    MailSender mailSender;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationContext context;
    List<User> users;

    public UserServiceTest() {
    }

    @Before
    public void setUp() {
        this.users = Arrays.asList(new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, 49, 0),
                new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, 50, 0),
                new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, 29),
                new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, 30),
                new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE));
    }




    @Test
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);
        userServiceImpl.upgradeLevels();
        List<User> updated = mockUserDao.getUpdated();
        Assert.assertThat(updated.size(), CoreMatchers.is(2));
        this.checkUserAndLevel((User)updated.get(0), "joytouch", Level.SILVER);
        this.checkUserAndLevel((User)updated.get(1), "madnite1", Level.GOLD);
        List<String> request = mockMailSender.getRequests();
        Assert.assertThat(request.size(), CoreMatchers.is(2));
        Assert.assertThat((String)request.get(0), CoreMatchers.is(((User)this.users.get(1)).getEmail()));
        Assert.assertThat((String)request.get(1), CoreMatchers.is(((User)this.users.get(3)).getEmail()));
    }




    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        Assert.assertThat(updated.getId(),CoreMatchers.is(expectedId));
        Assert.assertThat(updated.getLevel(),CoreMatchers.is(expectedLevel));
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = Mockito.mock(UserDao.class);
        Mockito.when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = (MailSender)Mockito.mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        (Mockito.verify(mockUserDao, Mockito.times(2))).update(any(User.class));
        (Mockito.verify(mockUserDao, Mockito.times(2))).update(any(User.class));
        (Mockito.verify(mockUserDao)).update(this.users.get(1));
        Assert.assertThat((this.users.get(1)).getLevel(), CoreMatchers.is(Level.SILVER));
        (Mockito.verify(mockUserDao)).update(this.users.get(3));
        Assert.assertThat((this.users.get(3)).getLevel(), CoreMatchers.is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        (Mockito.verify(mockMailSender, Mockito.times(2))).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        Assert.assertThat((mailMessages.get(0)).getTo()[0], CoreMatchers.is((this.users.get(1)).getEmail()));
        Assert.assertThat((mailMessages.get(1)).getTo()[0], CoreMatchers.is((this.users.get(3)).getEmail()));
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
        User userWithLevel = (User) this.users.get(4);
        User userWithoutLevel = (User) this.users.get(0);
        userWithoutLevel.setLevel((Level) null);
        this.userService.add(userWithLevel);
        this.userService.add(userWithoutLevel);
        User userWithLevelRead = this.userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = this.userDao.get(userWithoutLevel.getId());
        Assert.assertThat(userWithLevelRead.getLevel(), CoreMatchers.is(userWithLevel.getLevel()));
        Assert.assertThat(userWithoutLevelRead.getLevel(), CoreMatchers.is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() {
        this.userDao.deleteAll();
        Iterator var2 = this.users.iterator();

        while(var2.hasNext()) {
            User user = (User)var2.next();
            this.userDao.add(user);
        }

        try {
            this.testUserService.upgradeLevels();
            Assert.fail("TestUserServiceException expected");
        } catch (TestUserServiceException var3) {
        }

        this.checkLevelUpgraded((User)this.users.get(1), false);
    }
    @Test(
            expected = TransientDataAccessResourceException.class
    )
    public void readOnlyTransactionAttribute() {
        this.testUserService.getAll();
    }

    @Test
    @Transactional(
            propagation = Propagation.NEVER
    )
    public void transactionSync() {
        this.userService.deleteAll();
        this.userService.add((User)this.users.get(0));
        this.userService.add((User)this.users.get(1));
    }
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList();

        public List<String> getRequests() {
            return requests;
        }

        public void send(SimpleMailMessage mailMessage) throws MailException {
            this.requests.add(mailMessage.getTo()[0]);
        }

        public void send(SimpleMailMessage[] mailMessage) throws MailException {
        }
    }
    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated;

        private MockUserDao(List<User> users) {
            this.updated = new ArrayList();
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        public List<User> getAll() {
            return this.users;
        }

        public void update(User user) {
            this.updated.add(user);
        }

        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }
    static class TestUserService extends UserServiceImpl {
        private String id = "madnite1";

        TestUserService() {
        }

        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            } else {
                super.upgradeLevel(user);
            }
        }

        public List<User> getAll() {
            Iterator var2 = super.getAll().iterator();

            while(var2.hasNext()) {
                User user = (User)var2.next();
                super.update(user);
            }

            return null;
        }
    }

    static class TestUserServiceException extends RuntimeException {
        TestUserServiceException() {
        }
    }
}
