package my.com.byod.login.service;

import my.com.byod.login.domain.ApplicationUser;

public interface ApplicationUserService {

	ApplicationUser getUser(Long id);
	
	ApplicationUser findUserByUsername(String username);
	
	ApplicationUser findUserByUsernameAndEmail(String username, String email);

	ApplicationUser findUserByEmail(String email);
	
	ApplicationUser findUserByMobileNumber(String mobileNumber);
	
	ApplicationUser findUserById(Long id);
	
	Long createUser(ApplicationUser user, String role);
	
	int updatePassword(String updatedPassword, Long userId);
	
	void createPasswordResetTokenForUser(String token, Long userId);
}
