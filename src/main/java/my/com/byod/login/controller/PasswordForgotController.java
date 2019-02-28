package my.com.byod.login.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.service.ApplicationUserService;

@Controller
@RequestMapping("/forget-password")
public class PasswordForgotController {

	@Autowired
	private ApplicationUserService applicationUserService;

	@Autowired
	private MailSender mailSender;
	
	@GetMapping(value="")
	public ModelAndView forgetPasswordPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/forget-password");
		return model;
	}

	@PostMapping(value = "/send", produces = "application/json")
	public ResponseEntity<?> forgetPassword(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			JSONObject jsonData = new JSONObject(data);
			String email = jsonData.getString("email");
			
			if(email == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Email not found. Please try again later.");
			
			ApplicationUser user = applicationUserService.findUserByEmail(email);
			if(user != null) {
				String token = UUID.randomUUID().toString();
				applicationUserService.createPasswordResetTokenForUser(token, user.getId());
				mailSender.send(constructResetTokenEmail("http://localhost:8081", token, user.getId(), email));
				
				jsonResult.put("successMessage", "Email successfully send. Please check your email.");
			} else {
				jsonResult.put("errorMessage", "Email not exist in System.");			
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		}	
		return ResponseEntity.ok(jsonResult.toString());
	}

	private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, Long userId, String email) {
		String url = contextPath + "/reset-password/view?id=" + userId + "&token=" + token;
		String message = "Hello, below is the link for password reset. Kindly reset it within 30 minutes.";
		return constructEmail("Reset Password", message + " \r\n" + url, email);
	}

	private SimpleMailMessage constructEmail(String subject, String body, String receiverEmail) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(receiverEmail);

		return email;
	}

}
