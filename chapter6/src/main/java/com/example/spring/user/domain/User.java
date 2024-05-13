package com.example.spring.user.domain;

import lombok.*;


@Builder
@NoArgsConstructor
public class User {
	String id;
	String name;
	String password;
	String email;
	Level level;
	int login;
	int recommend;

	public User(String id, String name, String password, String email, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Level getLevel() {
		return this.level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return this.login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return this.recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
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
