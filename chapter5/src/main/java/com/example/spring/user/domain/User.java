package com.example.spring.user.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class User {
	String id;
	String name;
	String password;
	Level level;
	int login;
	int recommend;
	public User(String id, String name, String password,Level level,int login,int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if (nextLevel == null) {
			throw new IllegalStateException(this.level + "은  업그레이드가 불가능합니다");
		} else {
			this.level = nextLevel;
		}
	}

}
