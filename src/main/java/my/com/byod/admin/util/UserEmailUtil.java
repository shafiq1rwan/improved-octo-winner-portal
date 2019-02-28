package my.com.byod.admin.util;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
			helper.setText("Hi " + username
					+ ", \r\nThank you for registering with our BYOD solution. Here is you password for login :" + randomPass
					+ "\r\nPlease click on the link to login : <a href='http://localhost:8081/user/signin'>Byod</a>", true);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
