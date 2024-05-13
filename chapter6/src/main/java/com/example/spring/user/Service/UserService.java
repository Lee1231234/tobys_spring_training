package com.example.spring.user.Service;


import com.example.spring.user.domain.User;


public interface UserService {
    void add(User user);
    void upgradeLevels();
}
