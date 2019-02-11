package my.com.byod.login.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import my.com.byod.admin.entity.GroupCategory;
import my.com.byod.login.domain.ApplicationUser;

@Repository
public class ApplicationUserRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ApplicationUserRepository(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("jdbcTemplate cannot be null");
        }
		this.jdbcTemplate = jdbcTemplate;
	}
	
    @Transactional(readOnly = true)
    public ApplicationUser getUser(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[] {id}, applicationUserRowMapper);
    }
    
    @Transactional(readOnly = true)
    public ApplicationUser findUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", new Object[] {email}, applicationUserRowMapper);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }
    
    @Transactional(readOnly = true)
    public ApplicationUser findUserByMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            throw new IllegalArgumentException("Mobile number cannot be null");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE mobileNumber = ?", new Object[] {mobileNumber}, applicationUserRowMapper);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }
    
    @Transactional(readOnly = true)
    public ApplicationUser findUserByUsernameAndEmail(String username, String email) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }else if(email == null) {
        	throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND LOWER(email) = LOWER(?)", new Object[] {username, email}, applicationUserRowMapper);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }
    
    @Transactional(readOnly = true)
    public ApplicationUser findUserByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE LOWER(username) = LOWER(?)", new Object[] {username}, applicationUserRowMapper);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }

    public Long createUser(final ApplicationUser user) {
        if (user == null) {
            throw new IllegalArgumentException("user data cannot be null");
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
              .prepareStatement("INSERT INTO users(name,email,mobileNumber,address,username,password,enabled) VALUES(?,?,?,?,?,?,?)",  Statement.RETURN_GENERATED_KEYS);
              ps.setString(1, user.getName());
              ps.setString(2, user.getEmail());
              ps.setString(3, user.getMobileNumber());
              ps.setString(4, user.getAddress());
              ps.setString(5, user.getUsername());
              ps.setString(6, user.getPassword());
              ps.setBoolean(7, user.isEnabled());
              return ps;
            }, keyHolder);
     
            return (Long) keyHolder.getKey().longValue();
    }
    
	private RowMapper<ApplicationUser> applicationUserRowMapper = (rs, rowNum) -> {
		ApplicationUser user = new ApplicationUser();
		user.setId(rs.getLong("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setMobileNumber(rs.getString("mobileNumber"));
		user.setAddress(rs.getString("address"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setEnabled(rs.getBoolean("enabled"));
		return user;
	};

}
