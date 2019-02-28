package my.com.byod.login.domain;

import java.util.Calendar;
import java.util.Date;

public class PasswordResetToken {
	
	private Long id;
	private Long userId;
	private String token;
	private Date expiryDate;
	
	public PasswordResetToken() {
	}
	
	public PasswordResetToken(String token, Long userId, int minutes) {
		this.token = token;
		this.userId = userId;
		setExpiryDate(minutes);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setExpiryDate(int minutes) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, minutes);
		this.expiryDate = now.getTime();
	}

	public boolean isExpired() {
		return new Date().after(this.expiryDate);
	}

}
