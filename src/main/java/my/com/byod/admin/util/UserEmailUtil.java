package my.com.byod.admin.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class UserEmailUtil {

	@Autowired
	private JavaMailSender emailSender;

	public boolean sendUserRegisterPassword(String username, String randomPass ,String email) {

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(email);
			helper.setSubject("BYOD Login Password");
			helper.setText("Dear " + username
					+ ", \r\nThank you for registering with our BYOD solution. Here is you password for login :" + randomPass
					+ "\r\nPlease click on the link to login : <a href='http://localhost:8081/user/signin'>Byod</a>", true);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendActivationInfo(String username, JSONObject activationInfo , String brandId, String email) {

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(email);
			helper.setSubject(activationInfo.getString("device") + " Activation Info");
			helper.setText("Dear " + username + ","
					+ "<br><br>Kindly be informed that your activation info for "+ activationInfo.getString("device") +" are as followed,"
					+ "<br>Brand ID : " + brandId
					+ "<br>Activation ID : " + activationInfo.getString("activation_id")
					+ "<br>Activation Key: " + activationInfo.getString("activation_key")
					+ "<br><br>Please use the activation info to activate your device.", true);

			emailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
