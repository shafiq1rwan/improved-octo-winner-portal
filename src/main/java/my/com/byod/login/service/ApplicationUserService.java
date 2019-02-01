package my.com.byod.login.service;

import my.com.byod.login.domain.ApplicationUser;

public interface ApplicationUserService {

	ApplicationUser getUser(int id);
	
	ApplicationUser findUserByUsername(String username);
	
	ApplicationUser findUserByUsernameAndEmail(String username, String email);

	ApplicationUser findUserByEmail(String email);
	
	ApplicationUser findUserByMobileNumber(String mobileNumber);
	
	Long createUser(ApplicationUser user, String role);

}
