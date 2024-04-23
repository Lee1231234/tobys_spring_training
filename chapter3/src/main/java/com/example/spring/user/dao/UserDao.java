package com.example.spring.user.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.example.spring.user.domain.User;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDao {
	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};

	public UserDao() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(User user) {
		this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", new Object[]{user.getId(), user.getName(), user.getPassword()});
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
}