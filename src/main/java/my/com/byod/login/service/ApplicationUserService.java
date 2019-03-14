package my.com.byod.login.service;

import my.com.byod.login.domain.ApplicationUser;

public interface ApplicationUserService {

	ApplicationUser getUser(Long id);

	ApplicationUser findUserByUsername(String username);
	
	boolean findUserByUsername(String username, Long id);
	
	ApplicationUser findUserByUsernameAndEmail(String username, String email);

	ApplicationUser findUserByEmail(String email);
	
	boolean findUserByEmail(String email, Long id);
	
	ApplicationUser findUserByMobileNumber(String mobileNumber);
	
	boolean findUserByMobileNumber(String mobileNumber, Long id);
	
	ApplicationUser findUserById(Long id);
	
	Long createUser(ApplicationUser user, String role);
	
	int editUser(ApplicationUser user, String role);
	
	int updatePassword(String updatedPassword, Long userId);
	
	void createPasswordResetTokenForUser(String token, Long userId);
	
	int assignedNewUserToBrand(Long userId, Long brandId);
}
