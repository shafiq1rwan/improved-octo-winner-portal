package my.com.byod.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.domain.PasswordResetToken;
import my.com.byod.login.repository.ApplicationUserRepository;
import my.com.byod.login.repository.PasswordResetTokenRepository;

@Service
public class ApplicationUserServiceImp implements ApplicationUserService {

	private final ApplicationUserRepository applicationUserRepo;
	private final PasswordEncoder passwordEncoder;
	private final JdbcTemplate jdbcTemplate;
	private final PasswordResetTokenRepository passwordResetTokenRepo;

	@Autowired
	public ApplicationUserServiceImp(final ApplicationUserRepository applicationUserRepo,
			final PasswordEncoder passwordEncoder, final JdbcTemplate jdbcTemplate, 
			final PasswordResetTokenRepository passwordResetTokenRepo) {
		this.applicationUserRepo = applicationUserRepo;
		this.passwordEncoder = passwordEncoder;
		this.jdbcTemplate = jdbcTemplate;
		this.passwordResetTokenRepo = passwordResetTokenRepo;
	}

	@Override
	public ApplicationUser getUser(Long id) {
		return applicationUserRepo.getUser(id);
	}

	@Override
	public ApplicationUser findUserByUsername(String username) {
		return applicationUserRepo.findUserByUsername(username);
	}

	@Override
	public ApplicationUser findUserByEmail(String email) {
		return applicationUserRepo.findUserByEmail(email);
	}

	@Override
	public ApplicationUser findUserByUsernameAndEmail(String username, String email) {
		return applicationUserRepo.findUserByUsernameAndEmail(username, email);
	}

	@Override
	public ApplicationUser findUserByMobileNumber(String mobileNumber) {
		return applicationUserRepo.findUserByMobileNumber(mobileNumber);
	}

	@Override
	public Long createUser(ApplicationUser user, String role) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		Long userId = applicationUserRepo.createUser(user);

		if (userId != 0 || userId != null) {
			jdbcTemplate.update("INSERT INTO authorities(user_id, authority) VALUES (?,?)",
					new Object[] { userId, role });
		}
		return userId;
	}

	@Override
	public ApplicationUser findUserById(Long id) {
		return applicationUserRepo.findUserById(id);
	}

	@Override
	public int updatePassword(String updatedPassword, Long userId) {
		return applicationUserRepo.updatePassword(updatedPassword, userId);
	}

	@Override
	public void createPasswordResetTokenForUser(String token, Long userId) {
		PasswordResetToken myToken = new PasswordResetToken(token, userId, 30);
		passwordResetTokenRepo.createToken(myToken);
	}

}
