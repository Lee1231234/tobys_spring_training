package com.example.spring.config;

import com.example.spring.factorybean.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
@Component
public class AppRunner implements ApplicationRunner {
    @Autowired
    ApplicationContext applicationContext;

    private final Message message;

    @Autowired
    AppRunner(Message message){
        this.message = message;
    }
    @Override
    public void run(ApplicationArguments args){
        Message message = (Message)applicationContext.getBean("message");
        System.out.println(message.getText());
    }
}
