package com.example.spring;

import com.example.spring.config.AppRunner;
import com.example.spring.user.Service.UserService;
import com.example.spring.user.Service.UserServiceTx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
