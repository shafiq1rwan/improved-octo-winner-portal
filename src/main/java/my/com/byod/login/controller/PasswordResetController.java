package my.com.byod.login.controller;

import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.domain.PasswordResetToken;
import my.com.byod.login.repository.PasswordResetTokenRepository;
import my.com.byod.login.service.ApplicationUserService;

@Controller
@RequestMapping("/reset-password")
public class PasswordResetController {

	@Autowired
	private PasswordResetTokenRepository tokenRepo;

	@Autowired
	private ApplicationUserService applicationUserService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

/*	@GetMapping
	public ModelAndView resetPasswordView(@RequestParam(required = false) String token) {
		ModelAndView model = new ModelAndView();
		PasswordResetToken resetToken = tokenRepo.findByToken(token);
		if (resetToken == null) {
			model.addObject("error", "Could not find password reset token.");
		} else if (resetToken.isExpired()) {
			model.addObject("error", "Token has expired, please request a new password reset.");
		} else {
			model.addObject("token", resetToken.getToken());
		}

		model.setViewName("/reset-password");
		return model;
	}*/
	
	@GetMapping(value = "/view")
	public ModelAndView resetPasswordView(@RequestParam("id") Long id, @RequestParam("token") String token) {
		ModelAndView model = new ModelAndView();
		String result = validatePasswordResetToken(id, token);
		
		System.out.println("My ended :" + result);
		
	    if (result != null) {
			model.addObject("exceptionMsg", result);
			model.setViewName("/login");
	    	return model;
	    }
	    model.addObject("token", token);
		model.setViewName("/reset-password");
		return model;
	}
	
	private String validatePasswordResetToken(Long id, String token) {
	    PasswordResetToken passToken = tokenRepo.findByToken(token);
	    if ((passToken == null) || (passToken.getUserId() != id)) {
	        return "invalidToken";
	    }
	 
	    if (passToken.isExpired()){
	        return "expired";
	    }
	 
	    ApplicationUser user = applicationUserService.getUser(id);
	    Authentication auth = new UsernamePasswordAuthenticationToken(
	      user, null, Arrays.asList(
	      new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
	    SecurityContextHolder.getContext().setAuthentication(auth);
	    return null;
	}
	
	@RequestMapping(value = { "/reset" }, method = { RequestMethod.POST })
	public String resetPassword(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		
		try {	
			JSONObject jsonData = new JSONObject(data);

			if (!jsonData.getString("password").equals(jsonData.getString("confirm-password"))) {
				jsonResult.put("responseCode", "01");
				jsonResult.put("responseMessage", "Password not matched");
				jsonResult.put("responseRedirect", "/reset-password");
			}
			else {
				PasswordResetToken token = tokenRepo.findByToken(jsonData.getString("token"));
				ApplicationUser user = applicationUserService.findUserById(token.getUserId());
				String updatedPassword = passwordEncoder.encode(jsonData.getString("password"));
				applicationUserService.updatePassword(updatedPassword, user.getId());
				tokenRepo.remove(token);

				jsonResult.put("responseCode", "00");
				jsonResult.put("responseMessage", "SUCCESS RESET PASSWORD");
				jsonResult.put("responseRedirect", "/user/signin");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonResult.toString();
	}

}
