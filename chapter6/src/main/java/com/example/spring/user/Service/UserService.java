package com.example.spring.user.Service;


import com.example.spring.user.domain.User;

import java.util.List;


public interface UserService {
    void add(User var1);

    void deleteAll();

    void update(User var1);

    User get(String var1);

    List<User> getAll();

    void upgradeLevels();
}
