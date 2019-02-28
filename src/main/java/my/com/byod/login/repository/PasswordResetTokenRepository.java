package my.com.byod.login.repository;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.domain.PasswordResetToken;

@Repository
public class PasswordResetTokenRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public PasswordResetTokenRepository(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("jdbcTemplate cannot be null");
        }
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private RowMapper<PasswordResetToken> passwordResetTokenRowMapper = (rs, rowNum) -> {
		PasswordResetToken token = new PasswordResetToken();
		token.setId(rs.getLong("id"));
		token.setToken(rs.getString("token"));
		token.setUserId(rs.getLong("user_id"));
		
		java.util.Date existingDate = rs.getTimestamp("expiry_date");
		token.setExpiryDate(existingDate);

		return token;
	};
	
	public PasswordResetToken findByToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token is null");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM password_reset_token WHERE token = ?", new Object[] {token}, passwordResetTokenRowMapper);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
	}
	
	public int createToken(PasswordResetToken token) {
	   try {
		   return jdbcTemplate.update("INSERT INTO password_reset_token(token, user_id, expiry_date) VALUES (?,?,?)", new Object[] {token.getToken(), token.getUserId(), token.getExpiryDate()});
	   } catch(Exception ex) {
		   return 0;
	   }
	}
	
	public int remove(PasswordResetToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Token is null");
        }
        try {
            return jdbcTemplate.update("DELETE FROM password_reset_token WHERE id = ?", new Object[] {token.getId()});
        } catch (Exception ex) {
            return 0;
        }
	}
	
	
}
