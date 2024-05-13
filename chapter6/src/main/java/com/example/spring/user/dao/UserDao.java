package com.example.spring.user.dao;

import com.example.spring.user.domain.User;
import java.util.List;

public interface UserDao {
    void add(User var1);

    User get(String var1);

    List<User> getAll();

    void deleteAll();

    int getCount();

    public  void  update(User user);
}
