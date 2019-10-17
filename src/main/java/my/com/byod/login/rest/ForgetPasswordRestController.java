package my.com.byod.login.rest;

import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.UserEmailUtil;
import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.service.ApplicationUserService;

@RestController
@RequestMapping("/forget-password")
public class ForgetPasswordRestController {
	
	@Autowired
	private ApplicationUserService applicationUserService;

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private UserEmailUtil UserEmailUtil;

	@PostMapping(value = "/send-reset-email")
	public String sendForgetPasswordEmail(@RequestBody String data) {
		/*System.out.println("my data :" + data);*/
		JSONObject jsonResult = new JSONObject();
		
		try {
			JSONObject jsonData = new JSONObject(data);
			String email = jsonData.getString("email");
			System.out.println(email);
			
			if(email == null) {
				jsonResult.put("errorMessage", "Email not found.");
				jsonResult.put("responseCode", "01");	
			} else {
				ApplicationUser user = applicationUserService.findUserByEmail(email);
				String stringMail = UserEmailUtil.getConfigStr("portal_url");
				if(user != null) {
					String token = UUID.randomUUID().toString();
					applicationUserService.createPasswordResetTokenForUser(token, user.getId());
					mailSender.send(constructResetTokenEmail(stringMail, token, user.getId(), email));
					
					jsonResult.put("successMessage", "Email successfully send. Please check your email.");
					jsonResult.put("responseCode", "00");	
				} else {
					jsonResult.put("errorMessage", "Email not exist in System.");	
					jsonResult.put("responseCode", "01");	
				}
			}	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, Long userId, String email) {
		String url = contextPath + "/reset-password/view?id=" + userId + "&token=" + token;
		String message = "Hello, below is the link for password reset. Kindly reset it within 30 minutes.";
		return constructEmail("Reset Password", message + " \r\n" + url, email);
	}

	private SimpleMailMessage constructEmail(String subject, String body, String receiverEmail) {
		String stringMail = UserEmailUtil.getConfigStr("mail_sender");
		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom(stringMail);
		email.setSubject(subject);
		email.setText(body);
		email.setTo(receiverEmail);

		return email;
	}
}
