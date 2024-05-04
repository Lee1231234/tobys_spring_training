package com.example.spring.user.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			// 빌더패턴 이용
			User user = User.builder()
					.id(rs.getString("id"))
					.name(rs.getString("name"))
					.password(rs.getString("password"))
					.level(Level.valueOf(rs.getInt("level")))
					.login(rs.getInt("login"))
					.recommend(rs.getInt("recommend"))
					.build();


//
//			user = new User();
//			user.setId(rs.getString("id"));
//			user.setName(rs.getString("name"));
//			user.setPassword(rs.getString("password"));
//			user.setLevel(Level.valueOf(rs.getInt("level")));
//			user.setLogin(rs.getInt("login"));
//			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};

	public UserDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(User user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values(?,?,?,?,?,?)", new Object[]{user.getId(), user.getName(), user.getPassword(),user.getLevel().intValue(), user.getLogin(), user.getRecommend()});
	}

	public User get(String id) {
		return (User)this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, this.userMapper);
	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from users",Integer.class);
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}


	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name =?, password = ?, level = ?, login = ?, "
				+"recommend = ? where id = ? ", user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
		);
	}
}