package com.example.spring.user.Service;

import com.example.spring.user.dao.UserDao;
import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Iterator;
import java.util.List;

public class UserService implements UserLevelUpgradePolicy {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;
    private UserDao userDao;
    private MailSender mailSender;

    private PlatformTransactionManager transactionManager;
    public UserService() {
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = this.userDao.getAll();
            Iterator var4 = users.iterator();

            while(var4.hasNext()) {
                User user = (User)var4.next();
                if (this.canUpgradeLevel(user)) {
                    this.upgradeLevel(user);
                }
            }

            this.transactionManager.commit(status);
        } catch (RuntimeException var5) {
            this.transactionManager.rollback(status);
            throw var5;
        }
    }

    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case GOLD:
                return false;
            case SILVER:
                if (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD) {
                    return true;
                }

                return false;
            case BASIC:
                if (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }
    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
        this.mailSender.send(mailMessage);
    }
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        this.userDao.update(user);
    }
    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }

        this.userDao.add(user);
    }
}