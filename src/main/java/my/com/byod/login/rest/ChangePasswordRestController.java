package my.com.byod.login.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.service.ApplicationUserService;

@RestController
@RequestMapping("/change-password")
public class ChangePasswordRestController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private ApplicationUserService applicationUserService;
	
	@PostMapping(value = "/update")
	public ResponseEntity<?> changePasswordUser(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		
		try {
			
			JSONObject jsonData = new JSONObject(data);
			String userName = jsonData.getString("userName");
			String updatedPassword = passwordEncoder.encode(jsonData.getString("newPassword"));
			String confirmPassword = jsonData.getString("confirmPassword");
			String currentPassword = jsonData.getString("currentPassword");
			
			ApplicationUser user = applicationUserService.findUserByUsername(userName);
			Boolean passwordMatch = passwordEncoder.matches(currentPassword, user.getPassword());
			System.out.println("passwordMatch: "+passwordMatch);
			
			if(passwordMatch == true) {
				
				if(!jsonData.getString("newPassword").equalsIgnoreCase(confirmPassword)) {
					return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN).body("Confirmation password does not match. Please try again");
				}
				
				int updateStatus = applicationUserService.updatePassword(updatedPassword, user.getId());
				
				if(updateStatus > 0) {
					return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN).body("Password successfully update");
				}else {
					return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN)
							.body("Cannot change password. Please try again later");
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN)
						.body("Current Password does not match. Please try again");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN)
					.body("Error while update. Please try again later");
		}
		
	}
}
