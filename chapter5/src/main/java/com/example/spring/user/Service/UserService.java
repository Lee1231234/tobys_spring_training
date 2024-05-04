package com.example.spring.user.Service;

import com.example.spring.user.dao.UserDao;
import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;

import java.util.Iterator;
import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;
    private UserDao userDao;

    public UserService() {
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = this.userDao.getAll();
        Iterator var3 = users.iterator();

        while(var3.hasNext()) {
            User user = (User)var3.next();
            if (this.canUpgradeLevel(user)) {
                this.upgradeLevel(user);
            }
        }

    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case GOLD:
                return false;
            case SILVER:
                if (user.getRecommend() >= MIN_LOGCOUNT_FOR_SILVER) {
                    return true;
                }

                return false;
            case BASIC:
                if (user.getLogin() >= MIN_RECCOMEND_FOR_GOLD) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    private void upgradeLevel(User user) {
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