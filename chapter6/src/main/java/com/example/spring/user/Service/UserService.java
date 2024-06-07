package com.example.spring.user.Service;


import com.example.spring.user.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    void add(User var1);

    void deleteAll();

    void update(User var1);
    @Transactional(readOnly = true)
    User get(String var1);
    @Transactional(readOnly = true)
    List<User> getAll();

    void upgradeLevels();
}
